package com.stho.isee.ui.list

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Layout
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.stho.isee.R
import com.stho.isee.core.Entry
import com.stho.isee.databinding.FragmentListBinding

class ListFragment : Fragment() {

    private lateinit var viewModel: ListViewModel
    private lateinit var binding: FragmentListBinding
    private lateinit var adapter: ListViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[ListViewModel::class.java].also {
            val category = getCategoryFromArguments()
            it.setCategory(category)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        adapter = ListViewAdapter(requireActivity()) { entry -> onClickItem(entry) }
        binding = FragmentListBinding.inflate(inflater, container, false)
        binding.list.adapter = adapter
        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.showFab()
        viewModel.categoryLD.observe(viewLifecycleOwner, { category -> onObserveCategory(category) })
        viewModel.entriesLD.observe(viewLifecycleOwner) { entries -> onObserveEntries(entries) }
    }

    private fun onClickItem(entry: Entry) {
        Log.d("CLICK", "Entry: ${entry.title}")
        val action = ListFragmentDirections.actionNavListToNavViewDetails(entry.id)
        findNavController().navigate(action)
    }

    private fun onObserveCategory(category: String) {
        updateActionBar(getTitleString(category))
    }

    private fun onObserveEntries(entries: List<Entry>) {
        adapter.update(entries)
    }

    private fun updateActionBar(title: String) {
        actionBar?.also {
            it.title = title
            it.subtitle = null
        }
    }

    private val actionBar: ActionBar?
        get() = (requireActivity() as AppCompatActivity).supportActionBar

    private fun getTitleString(category: String): String {
        return getString(R.string.fragment_list_title, category)
    }

    private fun getCategoryFromArguments(defaultValue: String = DEFAULT_CATEGORY): String =
        arguments?.getString(KEY_CATEGORY, defaultValue) ?: defaultValue

    companion object {
        private const val KEY_CATEGORY = "CATEGORY"
        private const val DEFAULT_CATEGORY = "Computer"
    }
}

