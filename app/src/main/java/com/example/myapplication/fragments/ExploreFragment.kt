package com.example.myapplication.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapters.PlayerDiffCallback
import com.example.myapplication.adapters.PlayerPagingAdapter
import com.example.myapplication.databinding.FragmentExploreBinding
import com.example.myapplication.viewmodels.SharedViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ExploreFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var _binding: FragmentExploreBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @ExperimentalPagingApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)

        setupSpinner()

        val playerPagingAdapter = PlayerPagingAdapter(requireContext(), PlayerDiffCallback)
        binding.recyclerView.adapter = playerPagingAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        lifecycleScope.launch {
            sharedViewModel.getPlayerPaginatedFlow(requireContext()).collectLatest {
                playerPagingAdapter.submitData(it)
            }
        }

        return binding.root
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_item, //TODO popraviti izgled dropdown menija
            arrayOf("Players", "Teams")
        )
        binding.spinner.adapter = adapter
    }
}