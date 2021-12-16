package com.stho.isee.ui.details

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.stho.isee.R
import com.stho.isee.core.Entry
import com.stho.isee.core.Repository
import com.stho.isee.databinding.FragmentDetailsBinding
import com.stho.isee.utilities.toDateTimeString
import kotlinx.coroutines.*

class DetailsFragment : Fragment() {

    private lateinit var viewModel: DetailsViewModel
    private lateinit var binding: FragmentDetailsBinding
    private var passwordAnimationJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = createViewModel()
        setHasOptionsMenu(true)
        setupBackPressedHandler()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        binding.buttonShowPassword.setOnClickListener { onShowPassword() }
        binding.buttonEditPassword.setOnClickListener { onEditPassword() }
        binding.category.doAfterTextChanged { text -> onUpdateCategory(text) }
        binding.title.doAfterTextChanged { text -> onUpdateTitle(text) }
        binding.password.doAfterTextChanged { text -> onUpdatePassword(text) }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.hideFab()
        viewModel.entryLD.observe(viewLifecycleOwner) { entry -> onObserveEntry(entry) }
        viewModel.statusLD.observe(viewLifecycleOwner) { status -> onObserveStatus(status) }
        viewModel.passwordModeLD.observe(viewLifecycleOwner) { mode -> onObservePasswordMode(mode) }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.details, menu);
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.action_settings)?.isVisible = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home, R.id.home -> onHome()
            R.id.action_save -> onSave()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onHome() {
        if (shouldInterceptBackPressed()) {
            // TODO implement dialog ...
            showInterceptionDialog()
        }
    }

    /**
     * see: https://developer.android.com/guide/navigation/navigation-custom-back
     */
    private fun setupBackPressedHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (shouldInterceptBackPressed()) {
                    // TODO implement dialog ...
                    showInterceptionDialog()
                } else {
                    isEnabled = false
                    requireActivity().onBackPressed()
                }
            }
        })
    }

    private fun shouldInterceptBackPressed(): Boolean {
        return viewModel.isModified
    }

    private fun showInterceptionDialog() {
        AlertDialog.Builder(requireContext())
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle("Closing Activity")
            .setMessage("Are you sure you want to close this activity?")
            .setPositiveButton("Yes") { dialog, which ->
                // DetailsFragment.finish();
                TODO("Not yet implemented")
            }
            .setNegativeButton("No", null)
            .show();
    }

    private fun onObserveEntry(entry: Entry) {
        binding.category.setText(entry.category)
        binding.title.setText(entry.title)
        binding.url.setText(entry.url)
        binding.user.setText(entry.user)
        binding.password.setText(HIDDEN_PASSWORD)
        binding.description.setText(entry.description)
        binding.created.text = entry.created.toDateTimeString()
        binding.modified.text = entry.modified.toDateTimeString()
    }

    private fun onObserveStatus(status: Entry.Status) {
        updateActionBar(getTitleString(status))
    }

    private fun onObservePasswordMode(mode: DetailsViewModel.PasswordMode) {
        when (mode) {
            DetailsViewModel.PasswordMode.HIDDEN -> {
                binding.password.isEnabled = false
                binding.password.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.password.setText(HIDDEN_PASSWORD)
                binding.password.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.buttonEditPassword.isEnabled = true
                binding.buttonShowPassword.isEnabled = true
             }
            DetailsViewModel.PasswordMode.HINTS -> {
                binding.password.isEnabled = false
                binding.password.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.buttonEditPassword.isEnabled = false
                binding.buttonShowPassword.isEnabled = true
                displayPasswordAnimation()
            }
            DetailsViewModel.PasswordMode.EDIT -> {
                binding.password.setText("")
                binding.password.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.password.isEnabled = true
                binding.password.transformationMethod = DisplayLastInputPasswordTransformationMethod()
                binding.buttonEditPassword.isEnabled = false
                binding.buttonShowPassword.isEnabled = false
            }
        }
    }

    private fun updateActionBar(title: String) {
        actionBar?.also {
            it.title = title
            it.subtitle = null
        }
    }

    private val actionBar: ActionBar?
        get() = (requireActivity() as AppCompatActivity).supportActionBar

    private fun getTitleString(status: Entry.Status): String =
        when (status) {
            Entry.Status.NEW -> getString(R.string.fragment_details_title_new)
            Entry.Status.MODIFIED -> getString(R.string.fragment_details_title_modified)
            else -> getString(R.string.fragment_details_title)
        }

    private fun onShowPassword() {
        if (isPasswordAnimationRunning) {
            stopPasswordAnimation()
        } else {
            if (confirmUserAuthorization()) {
                viewModel.setPasswordMode(DetailsViewModel.PasswordMode.HINTS)
            }
        }
    }

    private fun onEditPassword() {
        viewModel.setPasswordMode(DetailsViewModel.PasswordMode.EDIT)
    }

    private fun onUpdateCategory(text: Editable?) {
        text?.also {
            viewModel.setCategory(it.toString())
        }
    }

    private fun onUpdateTitle(text: Editable?) {
        text?.also {
            viewModel.setTitle(it.toString())
        }
    }

    private fun onUpdatePassword(text: Editable?) {
        if (viewModel.passwordMode == DetailsViewModel.PasswordMode.EDIT) {
            viewModel.setPasswordMode(DetailsViewModel.PasswordMode.HIDDEN)
            text?.also {
                viewModel.setPassword(it.toString())
            }
        }
    }

    private val isPasswordAnimationRunning: Boolean
        get() = passwordAnimationJob?.isActive ?: false

    private fun stopPasswordAnimation() {
        passwordAnimationJob?.cancel()
    }

    private fun confirmUserAuthorization(): Boolean {
        // TODO: Implement
        return true
    }

    private fun displayPasswordAnimation() {
        passwordAnimationJob = CoroutineScope(Dispatchers.Default).launch {
            val length = viewModel.passwordLength
            for (index in 0 .. length) {
                if (viewModel.passwordMode == DetailsViewModel.PasswordMode.HINTS) {
                    val hint = viewModel.getPasswordHint(index)
                    binding.password.setText(hint)
                    delay(PASSWORD_HINT_DELAY)
                }
            }
            viewModel.setPasswordMode(DetailsViewModel.PasswordMode.HIDDEN)
        }
    }

    private fun onSave() {
        viewModel.save()
    }

    private fun createViewModel(): DetailsViewModel {
        val entry = getEntryFromArguments()
        val factory = DetailsViewModelFactory(requireActivity().application, entry)
        return ViewModelProvider(this, factory)[DetailsViewModel::class.java]
    }

    private fun getEntryFromArguments(): Entry {
        val id = getIdFromArguments()
        val repository = Repository.getInstance(requireContext())
        return if (id > 0) {
            repository.getEntry(id)
        } else {
            Entry.createNew()
        }
    }

    private fun getIdFromArguments(defaultValue: Int = 0): Int =
        arguments?.getInt(KEY_ID, defaultValue) ?: defaultValue

    companion object {
        private const val PASSWORD_HINT_DELAY = 1111L
        private const val HIDDEN_PASSWORD = "********"
        private const val KEY_ID = "ID"
    }
}