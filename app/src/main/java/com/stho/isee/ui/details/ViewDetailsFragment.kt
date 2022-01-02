package com.stho.isee.ui.details

import android.os.Bundle
import android.text.InputType
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.stho.isee.R
import com.stho.isee.authentication.AuthenticationHandler
import com.stho.isee.authentication.AuthenticationResult
import com.stho.isee.core.Entry
import com.stho.isee.core.Repository
import com.stho.isee.databinding.FragmentViewDetailsBinding
import kotlinx.coroutines.*

class ViewDetailsFragment : Fragment() {

    private lateinit var viewModel: ViewDetailsViewModel
    private lateinit var binding: FragmentViewDetailsBinding
    private var passwordAnimationJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = createViewModel()
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentViewDetailsBinding.inflate(inflater, container, false)
        binding.buttonShowPassword.setOnClickListener { onShowPassword() }
        binding.checkboxPlainText.setOnClickListener { onCheckPlainText() }
        binding.password.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
        binding.password.transformationMethod = PasswordTransformationMethod.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.hideFab()
        viewModel.entryLD.observe(viewLifecycleOwner) { entry -> onObserveEntry(entry) }
        viewModel.statusLD.observe(viewLifecycleOwner) { status -> onObserveStatus(status) }
        viewModel.passwordModeLD.observe(viewLifecycleOwner) { mode -> onObservePasswordMode(mode) }
        viewModel.passwordMirrorLD.observe(viewLifecycleOwner) { password -> onObservePasswordMirror(password) }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.view_details, menu);
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.action_settings)?.isVisible = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_edit -> onEdit()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setPasswordVisibilityAfterAuthentication(newMode: ViewDetailsViewModel.PasswordMode) {
        val handler = AuthenticationHandler(this) { result ->
            when (result) {
                AuthenticationResult.OK ->
                    viewModel.passwordMode = newMode

                AuthenticationResult.Cancel, AuthenticationResult.Error ->
                    viewModel.passwordMode = ViewDetailsViewModel.PasswordMode.HIDDEN
            }
        }
        handler.confirmAuthentication()
    }

    private fun onObserveEntry(entry: Entry) {
        binding.title.text = entry.title
        binding.url.text = entry.url
        binding.user.text = entry.user
        binding.description.text = entry.description
    }

    private fun onObservePasswordMirror(password: String) {
        binding.password.text = password
    }

    private fun onObserveStatus(status: Entry.Status) {
        updateActionBar(getTitleString(status))
    }

    private fun onObservePasswordMode(mode: ViewDetailsViewModel.PasswordMode) {
        when (mode) {
            ViewDetailsViewModel.PasswordMode.VISIBLE -> {
                binding.buttonShowPassword.isEnabled = true
                binding.password.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.checkboxPlainText.isChecked = true
            }
            ViewDetailsViewModel.PasswordMode.HIDDEN -> {
                binding.buttonShowPassword.isEnabled = true
                binding.password.transformationMethod = PasswordTransformationMethod.getInstance()
            }
            ViewDetailsViewModel.PasswordMode.HINTS -> {
                binding.buttonShowPassword.isEnabled = true
                displayPasswordAnimation()
            }
        }
        binding.checkboxPlainText.isChecked = ViewDetailsViewModel.isPasswordVisible(mode)
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
            setPasswordVisibilityAfterAuthentication(ViewDetailsViewModel.PasswordMode.HINTS)
        }
    }

    private fun onCheckPlainText() {
        // TODO: implement a consistent way to show the password after confirmation
        if (binding.checkboxPlainText.isChecked) {
            setPasswordVisibilityAfterAuthentication(ViewDetailsViewModel.PasswordMode.VISIBLE)
        } else {
            viewModel.passwordMode = ViewDetailsViewModel.PasswordMode.HIDDEN
        }
    }

    private val isPasswordAnimationRunning: Boolean
        get() = passwordAnimationJob?.isActive ?: false

    private fun stopPasswordAnimation() {
        passwordAnimationJob?.cancel()
        binding.password.transformationMethod = DisplayPasswordHintTransformationMethod(0)
        viewModel.passwordMode = ViewDetailsViewModel.PasswordMode.HIDDEN
    }

    private fun displayPasswordAnimation() {
        passwordAnimationJob = CoroutineScope(Dispatchers.Default).launch {
            val length = viewModel.passwordLength
            for (index in 0 .. length) {
                if (viewModel.passwordMode != ViewDetailsViewModel.PasswordMode.HINTS) {
                    break;
                } else {
                    binding.password.transformationMethod = DisplayPasswordHintTransformationMethod(index)
                    delay(PASSWORD_HINT_DELAY)
                }
            }
            binding.password.transformationMethod = DisplayPasswordHintTransformationMethod(0)
            viewModel.passwordMode = ViewDetailsViewModel.PasswordMode.HIDDEN
        }
    }

    private fun onEdit() {
        Log.d("EDIT", "Entry: ${viewModel.entry.title}")
        val entry = viewModel.entry
        val action = ViewDetailsFragmentDirections.actionNavViewDetailsToNavEditDetails(entry.id)
        findNavController().navigate(action)
    }

    private fun createViewModel(): ViewDetailsViewModel {
        val entry = getEntryFromArguments()
        val factory = DetailsViewModelFactory(requireActivity().application, entry)
        return ViewModelProvider(this, factory)[ViewDetailsViewModel::class.java]
    }

    private fun getEntryFromArguments(): Entry {
        val id = getIdFromArguments()
        val repository = Repository.getInstance(requireContext())
        return repository.getEntry(id)
    }

    private fun getIdFromArguments(defaultValue: Long = 0): Long =
        arguments?.getLong(KEY_ID, defaultValue) ?: defaultValue

    companion object {
        private const val PASSWORD_HINT_DELAY = 1111L
        private const val KEY_ID = "ID"
    }
}