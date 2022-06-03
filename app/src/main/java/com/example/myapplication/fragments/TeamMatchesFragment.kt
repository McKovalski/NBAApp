package com.example.myapplication.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.activities.TeamDetailsActivity
import com.example.myapplication.adapters.FilterRecyclerAdapter
import com.example.myapplication.adapters.MatchDiffCallback
import com.example.myapplication.adapters.TeamMatchPagingAdapter
import com.example.myapplication.databinding.FilterMatchesBottomSheetLayoutBinding
import com.example.myapplication.databinding.FragmentTeamMatchesBinding
import com.example.myapplication.helpers.Constants
import com.example.myapplication.helpers.TeamsHelper
import com.example.myapplication.models.Team
import com.example.myapplication.viewmodels.SharedViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val FRAGMENT_TYPE = "TeamMatches"

class TeamMatchesFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private var _binding: FragmentTeamMatchesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var team: Team

    private val teamMatchPagingAdapter by lazy {
        TeamMatchPagingAdapter(
            requireContext(),
            MatchDiffCallback
        )
    }

    private val filterAdapter by lazy {
        FilterRecyclerAdapter(
            requireContext(),
            mutableListOf(),
            mutableListOf(),
            this,
            FRAGMENT_TYPE
        )
    }

    private var isPostseason = false
    private val allTeams = mutableListOf<Team>()
    private var selectedSeason: Int = -1
    private var selectedTeamName: String? = null
    private val filters = mutableListOf<String>()
    private val filterTypes = mutableListOf<FilterType>()
    private var opponent: Team? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTeamMatchesBinding.inflate(inflater, container, false)

        sharedViewModel.getAllTeams(requireContext())

        team = (activity as TeamDetailsActivity).team
        teamMatchPagingAdapter.setTeam(team)

        binding.recyclerMatches.adapter = teamMatchPagingAdapter
        binding.recyclerMatches.layoutManager = LinearLayoutManager(requireContext())
        binding.filterActionBar.recycler.adapter = filterAdapter
        binding.filterActionBar.recycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.actionBar.buttonRegularSeason.isSelected = true
        binding.actionBar.buttonRegularSeason.setOnClickListener {
            if (!it.isSelected) {
                isPostseason = false
                it.apply { isSelected = !isSelected }
                binding.actionBar.buttonPlayoffs.apply { isSelected = !isSelected }
                getAllMatchesFlow()
            }
            binding.recyclerMatches.scrollToPosition(0)
        }
        binding.actionBar.buttonPlayoffs.setOnClickListener {
            if (!it.isSelected) {
                isPostseason = true
                it.apply { isSelected = !isSelected }
                binding.actionBar.buttonRegularSeason.apply { isSelected = !isSelected }
                getAllMatchesFlow()
            }
            binding.recyclerMatches.scrollToPosition(0)
        }

        lifecycleScope.launch {
            teamMatchPagingAdapter.loadStateFlow.collectLatest { loadState ->
                binding.progressBar.isVisible = loadState.refresh is LoadState.Loading
                binding.recyclerMatches.isVisible = loadState.refresh !is LoadState.Loading
            }
        }

        getAllMatchesFlow()

        return binding.root
    }

    override fun onResume() {
        setBottomSheetDialog()
        super.onResume()
    }

    // Reusing the bottom sheet dialog from Seasons fragment but with top input as opponent team
    private fun setBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.filter_matches_bottom_sheet_layout, null)
        val bottomSheetBinding = FilterMatchesBottomSheetLayoutBinding.bind(view)
        // Set the top input hint to "Opponent"
        bottomSheetBinding.teamInputLayout.setHint(getString(R.string.opponent))
        // Set the apply button color to teams primary color
        val (_, colorId) = TeamsHelper().getLogoAndColor(team.name)
        bottomSheetBinding.buttonApply.backgroundTintList =
            ContextCompat.getColorStateList(requireContext(), colorId)
        // Set the button as non clickable until user selects an item
        bottomSheetBinding.buttonApply.apply {
            alpha = 0.5f
            isClickable = false
            isEnabled = false
        }
        bottomSheetBinding.buttonCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetBinding.buttonApply.setOnClickListener {
            bottomSheetDialog.dismiss()
            val teamFilter = bottomSheetBinding.teamAutocomplete.text.toString()
            val seasonFilter = bottomSheetBinding.seasonAutocomplete.text.toString()
            if (teamFilter.isEmpty()) {
                selectedTeamName = null
                opponent = null
                val index = filterTypes.indexOf(FilterType.TEAM)
                if (index != -1) {
                    filters.removeAt(index)
                    filterTypes.removeAt(index)
                }
            } else {
                for (t in allTeams) {
                    if (t.full_name == selectedTeamName) {
                        opponent = t
                        break
                    }
                }
            }
            if (seasonFilter.isEmpty()) {
                selectedSeason = -1
                val index = filterTypes.indexOf(FilterType.SEASON)
                if (index != -1) {
                    filters.removeAt(index)
                    filterTypes.removeAt(index)
                }
            }
            getAllMatchesFlow()
            filterAdapter.updateFilters(filters)
            filterAdapter.updateFilterTypes(filterTypes)
            binding.filterActionBar.root.visibility = View.VISIBLE
            // Clear the autocomplete text views and...
            bottomSheetBinding.root.children.forEach {
                when (it) {
                    is TextInputLayout -> {
                        // TextInputLayout has a single child which is a FrameLayout
                        // our AutoCompleteTextView is a child of that FrameLayout
                        val frameLayout: FrameLayout = it.children.first() as FrameLayout
                        val autoCompleteTextView = frameLayout.children.first()
                        if (autoCompleteTextView is AutoCompleteTextView) {
                            autoCompleteTextView.text?.clear()
                            autoCompleteTextView.clearFocus()
                        }
                    }
                }
            }
            // ...disable the apply button
            bottomSheetBinding.buttonApply.apply {
                alpha = 0.5f
                isClickable = false
                isEnabled = false
            }
        }
        sharedViewModel.allTeams.observe(viewLifecycleOwner) {
            allTeams.clear()
            allTeams.addAll(it)
            Log.d("all teams", allTeams.toString())
            val teamNames = allTeams.map { t -> t.full_name }.filter { s -> s != team.full_name }
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
                    isEnabled = true
                }
                val index = filterTypes.indexOf(FilterType.TEAM)
                if (index != -1) {
                    filters.removeAt(index)
                    filters.add(index, selectedTeamName!!)
                } else {
                    filterTypes.add(FilterType.TEAM)
                    filters.add(selectedTeamName!!)
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
                isEnabled = true
            }
            val index = filterTypes.indexOf(FilterType.SEASON)
            if (index != -1) {
                filters.removeAt(index)
                filters.add(index, selectedSeason.toString())
            } else {
                filterTypes.add(FilterType.SEASON)
                filters.add(selectedSeason.toString())
            }
        }
        bottomSheetDialog.setContentView(view)
        binding.actionBar.iconFilter.setOnClickListener {
            bottomSheetDialog.show()
        }
    }

    private fun getAllMatchesFlow() {
        lifecycleScope.launch {
            sharedViewModel.getMatchesFlowOpponentFilter(
                isPostseason,
                getTeamIds(),
                getSeasons(),
                opponent
            )
                .collectLatest { pagingData ->
                    teamMatchPagingAdapter.submitData(pagingData)
                }
        }
        binding.recyclerMatches.scrollToPosition(0)
    }

    private fun getTeamIds(): Array<Int> {
        return arrayOf(team.id)
    }

    private fun getSeasons(): Array<Int>? {
        var seasons: Array<Int>? = null
        if (selectedSeason != -1) {
            seasons = arrayOf(selectedSeason)
        }
        return seasons
    }

    fun removeFilter(filterType: FilterType) {
        val index = filterTypes.indexOf(filterType)
        if (index != -1) {
            filters.removeAt(index)
            filterTypes.removeAt(index)
        }
        if (filters.isEmpty()) {
            binding.filterActionBar.root.visibility = View.GONE
        }
        when (filterType) {
            FilterType.SEASON -> selectedSeason = -1
            FilterType.TEAM -> selectedTeamName = null
        }
        getAllMatchesFlow()
        filterAdapter.updateFilters(filters)
        filterAdapter.updateFilterTypes(filterTypes)
    }
}