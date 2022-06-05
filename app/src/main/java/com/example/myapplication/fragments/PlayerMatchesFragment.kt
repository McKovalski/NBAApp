package com.example.myapplication.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.core.view.children
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.activities.PlayerDetailsActivity
import com.example.myapplication.adapters.FilterRecyclerAdapter
import com.example.myapplication.adapters.PlayerStatsRecyclerAdapter
import com.example.myapplication.adapters.StatDiffCallback
import com.example.myapplication.adapters.StatPagingAdapter
import com.example.myapplication.databinding.FilterMatchesBottomSheetLayoutBinding
import com.example.myapplication.databinding.FragmentPlayerMatchesBinding
import com.example.myapplication.databinding.PlayerStatsBottomSheetLayoutBinding
import com.example.myapplication.helpers.Constants
import com.example.myapplication.helpers.DateTimeHelper
import com.example.myapplication.helpers.TeamsHelper
import com.example.myapplication.models.Player
import com.example.myapplication.models.Stats
import com.example.myapplication.models.Team
import com.example.myapplication.viewmodels.SharedViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val FRAGMENT_TYPE = "PlayerMatches"

class PlayerMatchesFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private var _binding: FragmentPlayerMatchesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var player: Player

    private val statPagingAdapter by lazy {
        StatPagingAdapter(
            requireContext(),
            this,
            StatDiffCallback
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
    private val statsMap = linkedMapOf<String, Float>(
        "min" to 0f,
        "fgm" to 0f,
        "fga" to 0f,
        "fg3m" to 0f,
        "fg3a" to 0f,
        "ftm" to 0f,
        "fta" to 0f,
        "oreb" to 0f,
        "dreb" to 0f,
        "reb" to 0f,
        "ast" to 0f,
        "stl" to 0f,
        "blk" to 0f,
        "turnover" to 0f,
        "pf" to 0f,
        "pts" to 0f,
        "fg_pct" to 0f,
        "fg3_pct" to 0f,
        "ft_pct" to 0f
    )
    /*private val playerStatsAdapter by lazy {
        PlayerStatsRecyclerAdapter(
            requireContext(),
            statsMap,
            false
        )
    }*/

    private val allTeams = mutableListOf<Team>()
    private var isPostseason = false
    private var selectedSeason: Int = -1
    private val filters = mutableListOf<String>()
    private val filterTypes = mutableListOf<FilterType>()
    private lateinit var statsBottomSheetDialog: BottomSheetDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerMatchesBinding.inflate(inflater, container, false)

        sharedViewModel.getAllTeams(requireContext())
        sharedViewModel.allTeams.observe(viewLifecycleOwner) {
            allTeams.clear()
            allTeams.addAll(it)
            statPagingAdapter.updateTeams(allTeams)
        }

        player = (activity as PlayerDetailsActivity).player

        binding.recyclerMatches.adapter = statPagingAdapter
        binding.recyclerMatches.layoutManager = LinearLayoutManager(requireContext())
        binding.filterActionBar.recycler.adapter = filterAdapter
        binding.filterActionBar.recycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        setBottomSheetDialog()

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
            statPagingAdapter.loadStateFlow.collectLatest { loadState ->
                binding.progressBar.isVisible = loadState.refresh is LoadState.Loading
                binding.recyclerMatches.isVisible = loadState.refresh !is LoadState.Loading

                if (loadState.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && statPagingAdapter.itemCount < 1) {
                    binding.emptyStatePlaceholder.root.visibility = View.VISIBLE
                } else {
                    binding.emptyStatePlaceholder.root.visibility = View.GONE
                }
            }
        }

        getAllMatchesFlow()

        return binding.root
    }

    // Reusing the bottom sheet dialog from Seasons fragment but only with seasons input
    private fun setBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.filter_matches_bottom_sheet_layout, null)
        val bottomSheetBinding = FilterMatchesBottomSheetLayoutBinding.bind(view)
        // Hide Teams input
        bottomSheetBinding.teamInputLayout.isGone = true
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
            val seasonFilter = bottomSheetBinding.seasonAutocomplete.text.toString()
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
        sharedViewModel.playerSeasons.observe(viewLifecycleOwner) {
            val seasonsAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                it
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
        }
        bottomSheetDialog.setContentView(view)
        binding.actionBar.iconFilter.setOnClickListener {
            bottomSheetDialog.show()
        }
    }

    private fun getAllMatchesFlow() {
        lifecycleScope.launch {
            sharedViewModel.getStatsForPlayerFlow(isPostseason, getPlayerIds(), getSeasons())
                .collectLatest { pagingData ->
                    statPagingAdapter.submitData(pagingData)
                }
        }
        binding.recyclerMatches.scrollToPosition(0)
    }

    private fun getPlayerIds(): Array<Int> {
        return arrayOf(player.id)
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
            else -> print("")
        }
        getAllMatchesFlow()
        filterAdapter.updateFilters(filters)
        filterAdapter.updateFilterTypes(filterTypes)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun showStatsBottomDialog(stat: Stats) {
        statsMap["min"] = stat.min.split(":")[0].toFloat()
        statsMap["fgm"] = stat.fgm.toFloat()
        statsMap["fga"] = stat.fga.toFloat()
        statsMap["fg3m"] = stat.fg3m.toFloat()
        statsMap["fg3a"] = stat.fg3a.toFloat()
        statsMap["ftm"] = stat.ftm.toFloat()
        statsMap["fta"] = stat.fta.toFloat()
        statsMap["oreb"] = stat.oreb.toFloat()
        statsMap["dreb"] = stat.dreb.toFloat()
        statsMap["reb"] = stat.reb.toFloat()
        statsMap["ast"] = stat.ast.toFloat()
        statsMap["stl"] = stat.stl.toFloat()
        statsMap["blk"] = stat.blk.toFloat()
        statsMap["turnover"] = stat.turnover.toFloat()
        statsMap["pf"] = stat.pf.toFloat()
        statsMap["pts"] = stat.pts.toFloat()
        statsMap["fg_pct"] = if (stat.fg_pct <= 1) stat.fg_pct * 100 else stat.fg_pct
        statsMap["fg3_pct"] = if (stat.fg3_pct <= 1) stat.fg3_pct * 100 else stat.fg3_pct
        statsMap["ft_pct"] = if (stat.ft_pct <= 1) stat.ft_pct * 100 else stat.ft_pct
        Log.d("Stats", statsMap.toString())

        statsBottomSheetDialog = BottomSheetDialog(requireContext())
        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.player_stats_bottom_sheet_layout, null)
        val bottomSheetBinding = PlayerStatsBottomSheetLayoutBinding.bind(view)
        val playerStatsAdapter = PlayerStatsRecyclerAdapter(
            requireContext(),
            statsMap,
            false
        )
        bottomSheetBinding.statsRecyclerView.adapter = playerStatsAdapter
        bottomSheetBinding.statsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        bottomSheetBinding.header.apply {
            date.text = DateTimeHelper().getDayMonthYear(stat.game.date)
            playerName.text = stat.player.fullName()
            val homeTeam = allTeams.first { t -> t.id == stat.game.home_team_id }
            val (homeLogoId, _) = TeamsHelper().getLogoAndColor(homeTeam.name)
            firstTeamAbbr.text = homeTeam.abbreviation
            firstTeamLogo.setImageResource(homeLogoId)
            val awayTeam = allTeams.first { t -> t.id == stat.game.visitor_team_id }
            val (awayLogoId, _) = TeamsHelper().getLogoAndColor(awayTeam.name)
            secondTeamAbbr.text = homeTeam.abbreviation
            secondTeamLogo.setImageResource(awayLogoId)
        }
        statsBottomSheetDialog.setContentView(view)
        statsBottomSheetDialog.show()
    }
}