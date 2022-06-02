package com.example.myapplication.fragments

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapters.MatchDiffCallback
import com.example.myapplication.adapters.MatchPagingAdapter
import com.example.myapplication.databinding.AddImageBottomSheetLayoutBinding
import com.example.myapplication.databinding.FilterMatchesBottomSheetLayoutBinding
import com.example.myapplication.databinding.FragmentSeasonsBinding
import com.example.myapplication.helpers.Constants
import com.example.myapplication.models.Team
import com.example.myapplication.viewmodels.SharedViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
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
    private val allTeams = mutableListOf<Team>()
    private var selectedSeason: Int = -1
    private var selectedTeamName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSeasonsBinding.inflate(inflater, container, false)

        binding.recyclerMatches.adapter = matchPagingAdapter
        binding.recyclerMatches.layoutManager = LinearLayoutManager(requireContext())

        setBottomSheetDialog()

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

    override fun onResume() {
        sharedViewModel.getAllTeams(requireContext())
        super.onResume()
    }

    private fun setBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.filter_matches_bottom_sheet_layout, null)
        val bottomSheetBinding = FilterMatchesBottomSheetLayoutBinding.bind(view)
        // Set the button as non clickable until user selects an item
        bottomSheetBinding.buttonApply.apply {
            alpha = 0.5f
            isClickable = false
        }
        bottomSheetBinding.buttonCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetBinding.buttonApply.setOnClickListener {
            var teamIds: Array<Int>? = null
            if (selectedTeamName != null) {
                for (team in allTeams) {
                    if (team.full_name == selectedTeamName) {
                        teamIds = arrayOf(team.id)
                        break
                    }
                }
            }
            var seasons: Array<Int>? = null
            if (selectedSeason != -1) {
                seasons = arrayOf(selectedSeason)
            }
            lifecycleScope.launch {
                sharedViewModel.getAllMatchesFlow(isPostseason, teamIds, seasons)
                    .collectLatest { pagingData ->
                        matchPagingAdapter.submitData(pagingData)
                    }
            }
            bottomSheetDialog.dismiss()
        }
        sharedViewModel.allTeams.observe(viewLifecycleOwner) {
            allTeams.clear()
            allTeams.addAll(it)
            Log.d("all teams", allTeams.toString())
            val teamNames = allTeams.map { t -> t.full_name }
            val teamsAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                teamNames
            )
            bottomSheetBinding.teamAutocomplete.setAdapter(teamsAdapter)
            bottomSheetBinding.teamAutocomplete.setOnItemClickListener { adapterView, _, position, _ ->
                selectedTeamName = adapterView?.getItemAtPosition(position) as String
                // Enable the apply button
                bottomSheetBinding.buttonApply.apply {
                    alpha = 1f
                    isClickable = true
                }
            }
        }
        val seasonsAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            (Constants().LAST_SEASON downTo 1979).toList()
        )
        bottomSheetBinding.seasonAutocomplete.setAdapter(seasonsAdapter)
        bottomSheetBinding.seasonAutocomplete.setOnItemClickListener { adapterView, _, position, _ ->
            selectedSeason = adapterView?.getItemAtPosition(position) as Int
            // Enable the apply button
            bottomSheetBinding.buttonApply.apply {
                alpha = 1f
                isClickable = true
            }
        }
        bottomSheetDialog.setContentView(view)
        binding.actionBar.iconFilter.setOnClickListener {
            bottomSheetDialog.show()
        }
    }
}