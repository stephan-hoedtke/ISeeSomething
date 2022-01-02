package com.stho.isee.ui.details

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
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.stho.isee.R
import com.stho.isee.authentication.AuthenticationHandler
import com.stho.isee.authentication.AuthenticationResult
import com.stho.isee.core.Entry
import com.stho.isee.core.Repository
import com.stho.isee.databinding.FragmentEditDetailsBinding
import com.stho.isee.utilities.toDateTimeString
import kotlinx.coroutines.*

class EditDetailsFragment : Fragment() {

    private lateinit var viewModel: EditDetailsViewModel
    private lateinit var binding: FragmentEditDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = createViewModel()
        setHasOptionsMenu(true)
        setupBackPressedHandler()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentEditDetailsBinding.inflate(inflater, container, false)
        binding.category.doAfterTextChanged { text -> onUpdateCategory(text) }
        binding.title.doAfterTextChanged { text -> onUpdateTitle(text) }
        binding.user.doAfterTextChanged { text -> onUpdateUser(text) }
        binding.url.doAfterTextChanged { text -> onUpdateUrl(text) }
        binding.description.doAfterTextChanged { text -> onUpdateDescription(text) }
        binding.password.doAfterTextChanged { text -> onUpdatePassword(text) }
        binding.password.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
        binding.password.transformationMethod = DisplayLastInputPasswordTransformationMethod()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.hideFab()
        viewModel.entryLD.observe(viewLifecycleOwner) { entry -> onObserveEntry(entry) }
        viewModel.statusLD.observe(viewLifecycleOwner) { status -> onObserveStatus(status) }
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
        return viewModel.isDirty
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
        updateTextEdit(binding.category, entry.category)
        updateTextEdit(binding.category, entry.category)
        updateTextEdit(binding.title, entry.title)
        updateTextEdit(binding.url, entry.url)
        updateTextEdit(binding.user, entry.user)
        updateTextEdit(binding.description, entry.description)
        binding.created.text = entry.created.toDateTimeString()
        binding.modified.text = entry.modified.toDateTimeString()
    }

    private fun onObservePasswordMirror(password: String) {
        if (password != binding.password.text.toString()) {
            updateTextEdit(binding.password, password)
        }
    }

    private fun updateTextEdit(field: TextInputEditText, text: String) {
        if (text != field.text.toString()) {
            field.setText(text)
        }
    }

    private fun onObserveStatus(status: Entry.Status) {
        updateActionBar(getTitleString(status))
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

    private fun onUpdateUser(text: Editable?) {
        text?.also {
            viewModel.setUser(it.toString())
        }
    }

    private fun onUpdateUrl(text: Editable?) {
        text?.also {
            viewModel.setUrl(it.toString())
        }
    }

    private fun onUpdateDescription(text: Editable?) {
        text?.also {
            viewModel.setDescription(it.toString())
        }
    }

    private fun onUpdatePassword(text: Editable?) {
        text?.also {
            viewModel.passwordMirror = it.toString()
        }
    }

    private fun onSave() {
        viewModel.save()
    }

    private fun createViewModel(): EditDetailsViewModel {
        val entry = getEntryFromArguments()
        val factory = DetailsViewModelFactory(requireActivity().application, entry)
        return ViewModelProvider(this, factory)[EditDetailsViewModel::class.java]
    }

    private fun getEntryFromArguments(): Entry {
        val id = getIdFromArguments()
        return if (id == 0L) {
            Entry.createNew()
        } else {
            val repository = Repository.getInstance(requireContext())
            repository.getEntry(id)
        }
    }

    private fun getIdFromArguments(defaultValue: Long = 0): Long =
        arguments?.getLong(KEY_ID, defaultValue) ?: defaultValue

    companion object {
        private const val KEY_ID = "ID"
    }
}