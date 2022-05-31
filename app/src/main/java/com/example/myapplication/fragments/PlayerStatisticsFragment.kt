package com.example.myapplication.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.activities.PlayerDetailsActivity
import com.example.myapplication.adapters.PlayerStatsRecyclerAdapter
import com.example.myapplication.databinding.FragmentPlayerStatisticsBinding
import com.example.myapplication.databinding.StatsTableRowBinding
import com.example.myapplication.models.Player
import com.example.myapplication.viewmodels.SharedViewModel
import java.util.*

class PlayerStatisticsFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private var _binding: FragmentPlayerStatisticsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var player: Player
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
    private val statsAdapter by lazy {
        PlayerStatsRecyclerAdapter(
            requireContext(),
            statsMap
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerStatisticsBinding.inflate(inflater, container, false)

        binding.statsRecyclerView.adapter = statsAdapter
        binding.statsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        player = (activity as PlayerDetailsActivity).player

        val seasons = mutableListOf<Int>()
        sharedViewModel.getPlayerSeasons(player.id)
        sharedViewModel.playerSeasons.observe(viewLifecycleOwner) {
            seasons.addAll(it)
            seasons.sortDescending()
            Log.d("seasons", seasons.toString())
            if (seasons.isNullOrEmpty()) {
                binding.seasonsMask.spinnerSeason.setText(getString(R.string.n_a))

            } else {
                setupSpinner(seasons)
                sharedViewModel.getSeasonAveragesForPlayer(player.id, seasons[0])
            }
        }

        return binding.root
    }

    override fun onResume() {
        sharedViewModel.seasonAveragesForPlayer.observe(viewLifecycleOwner) {
            if (it != null) {
                statsMap["min"] = it.getMinutes()
                statsMap["fgm"] = it.fgm
                statsMap["fga"] = it.fga
                statsMap["fg3m"] = it.fg3m
                statsMap["fg3a"] = it.fg3a
                statsMap["ftm"] = it.ftm
                statsMap["fta"] = it.fta
                statsMap["oreb"] = it.oreb
                statsMap["dreb"] = it.dreb
                statsMap["reb"] = it.reb
                statsMap["ast"] = it.ast
                statsMap["stl"] = it.stl
                statsMap["blk"] = it.blk
                statsMap["turnover"] = it.turnover
                statsMap["pf"] = it.pf
                statsMap["pts"] = it.pts
                statsMap["fg_pct"] = it.fg_pct * 100
                statsMap["fg3_pct"] = it.fg3_pct * 100
                statsMap["ft_pct"] = it.ft_pct * 100
                Log.d("Stats", statsMap.toString())

                statsAdapter.updateStats(statsMap)
            } else {
                statsAdapter.noStatsFound(true)
            }
        }

        super.onResume()
    }

    private fun setupSpinner(seasons: List<Int>) {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            seasons
        )
        binding.seasonsMask.spinnerSeason.setAdapter(adapter)
        binding.seasonsMask.spinnerSeason.setText(seasons[0].toString(), false)
        binding.seasonsMask.spinnerSeason.setOnItemClickListener { adapterView, _, position, _ ->
            val season = adapterView?.getItemAtPosition(position) as Int
            sharedViewModel.getSeasonAveragesForPlayer(player.id, season)
        }
    }
}