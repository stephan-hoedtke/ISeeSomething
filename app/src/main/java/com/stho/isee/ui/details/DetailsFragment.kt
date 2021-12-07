package com.stho.isee.ui.details

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.stho.isee.R
import com.stho.isee.core.Entry
import com.stho.isee.core.Repository
import com.stho.isee.databinding.FragmentDetailsBinding
import com.stho.isee.utilities.toDateTimeString

class DetailsFragment : Fragment() {

    private lateinit var viewModel: DetailsViewModel
    private lateinit var binding: FragmentDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = createViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.entryLD.observe(viewLifecycleOwner) { entry -> onObserveEntry(entry) }
    }

    private fun onObserveEntry(entry: Entry) {
        binding.category.setText(entry.category)
        binding.title.setText(entry.title)
        binding.url.setText(entry.url)
        binding.user.setText(entry.user)
        binding.value.setText(HIDDEN_PASSWORD)
        binding.description.setText(entry.description)
        binding.created.text = entry.created.toDateTimeString()
        binding.modified.text = entry.modified.toDateTimeString()
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
        private const val HIDDEN_PASSWORD = "********"
        private const val KEY_ID = "ID"
    }
}