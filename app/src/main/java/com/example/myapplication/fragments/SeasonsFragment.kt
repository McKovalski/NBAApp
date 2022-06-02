package com.example.myapplication.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapters.MatchDiffCallback
import com.example.myapplication.adapters.MatchPagingAdapter
import com.example.myapplication.databinding.FragmentSeasonsBinding
import com.example.myapplication.viewmodels.SharedViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class SeasonsFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private var _binding: FragmentSeasonsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val matchPagingAdapter by lazy {
        MatchPagingAdapter(
            requireContext(),
            MatchDiffCallback
        )
    }

    private var isPostseason = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSeasonsBinding.inflate(inflater, container, false)

        binding.recyclerMatches.adapter = matchPagingAdapter
        binding.recyclerMatches.layoutManager = LinearLayoutManager(requireContext())

        binding.actionBar.buttonRegularSeason.isSelected = true
        binding.actionBar.buttonRegularSeason.setOnClickListener {
            if (!it.isSelected) {
                isPostseason = false
                it.apply { isSelected = !isSelected }
                binding.actionBar.buttonPlayoffs.apply { isSelected = !isSelected }
                lifecycleScope.launch {
                    sharedViewModel.getAllMatchesFlow(isPostseason).collectLatest { pagingData ->
                        matchPagingAdapter.submitData(pagingData)
                    }
                }
            }
            binding.recyclerMatches.scrollToPosition(0)
        }
        binding.actionBar.buttonPlayoffs.setOnClickListener {
            if (!it.isSelected) {
                isPostseason = true
                it.apply { isSelected = !isSelected }
                binding.actionBar.buttonRegularSeason.apply { isSelected = !isSelected }
                lifecycleScope.launch {
                    sharedViewModel.getAllMatchesFlow(isPostseason).collectLatest { pagingData ->
                        matchPagingAdapter.submitData(pagingData)
                    }
                }
            }
            binding.recyclerMatches.scrollToPosition(0)
        }

        lifecycleScope.launch {
            matchPagingAdapter.loadStateFlow.collectLatest { loadState ->
                binding.progressBar.isVisible = loadState.refresh is LoadState.Loading
                binding.recyclerMatches.isVisible = loadState.refresh !is LoadState.Loading
            }
        }

        lifecycleScope.launch {
            sharedViewModel.getAllMatchesFlow(isPostseason).collectLatest { pagingData ->
                matchPagingAdapter.submitData(pagingData)
            }
        }

        return binding.root
    }
}