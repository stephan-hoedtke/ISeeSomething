package com.stho.isee.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.stho.isee.R
import com.stho.isee.authentication.AuthenticationHandler
import com.stho.isee.authentication.AuthenticationResult
import com.stho.isee.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: HomeListViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        adapter = HomeListViewAdapter(requireActivity()) { entry -> onClickItem(entry) }
        binding.list.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.showFab()
        viewModel.categoriesLD.observe(viewLifecycleOwner) { categories -> onObserveCategories(categories) }
        confirmAuthentication()
    }

    private fun confirmAuthentication() {
        // TODO: implement loop: retry -> error... cancel --> close app
        if (!viewModel.isAuthenticated) {
            val handler = AuthenticationHandler(this) { result ->
                when (result) {
                    AuthenticationResult.OK -> {
                        viewModel.isAuthenticated = true
                    }
                    AuthenticationResult.Cancel -> {
                        showExitDialog()
                    }
                    AuthenticationResult.Error -> {
                        showExitDialog()
                    }
                }
            }
            handler.confirmAuthentication()
        }
    }

    private fun showExitDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Exit Application?")
            .setMessage("Click yes to exit!")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                exit()
            }
            .setNegativeButton("No") { dialog, id ->
                dialog.cancel();
                confirmAuthentication()
            }
            .create()
            .show()
    }

    private fun exit() {
        requireActivity().finishAffinity()
    }

    private fun onObserveCategories(categories: List<HomeListEntry>) {
        adapter.update(categories)
    }

    private fun onClickItem(entry: HomeListEntry) {
        Log.d("CLICK", "Entry: ${entry.category}")
        val action = HomeFragmentDirections.actionNavHomeToNavList(entry.category)
        findNavController().navigate(action)
    }
}
