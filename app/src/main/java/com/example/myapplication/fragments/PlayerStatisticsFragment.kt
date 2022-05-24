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
import com.example.myapplication.R
import com.example.myapplication.activities.PlayerDetailsActivity
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
    private lateinit var fragmentContainer: ViewGroup

    private lateinit var player: Player
    private var playersLastSeason = 0
    private val statsMap = linkedMapOf<String, MutableList<Float>>(
        "min" to mutableListOf(),
        "fgm" to mutableListOf(),
        "fg3m" to mutableListOf(),
        "fg3a" to mutableListOf(),
        "ftm" to mutableListOf(),
        "fta" to mutableListOf(),
        "oreb" to mutableListOf(),
        "reb" to mutableListOf(),
        "ast" to mutableListOf(),
        "stl" to mutableListOf(),
        "blk" to mutableListOf(),
        "turnover" to mutableListOf(),
        "pf" to mutableListOf(),
        "pts" to mutableListOf(),
        "fg_pct" to mutableListOf(),
        "fg3_pct" to mutableListOf(),
        "ft_pct" to mutableListOf()
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerStatisticsBinding.inflate(inflater, container, false)
        fragmentContainer = container!!

        player = (activity as PlayerDetailsActivity).player

        //setupSpinner()

        return binding.root
    }

    override fun onResume() {
        sharedViewModel.getSeasonAveragesForPlayer(237, null)
        sharedViewModel.seasonAveragesForPlayer.observe(viewLifecycleOwner) {
            statsMap.forEach { (_, list) ->
                list.clear()
            }
            for (seasonAverages in it) {
                statsMap["min"]?.add(seasonAverages.getMinutes())
                statsMap["fgm"]?.add(seasonAverages.fgm)
                statsMap["fg3m"]?.add(seasonAverages.fg3m)
                statsMap["fg3a"]?.add(seasonAverages.fg3a)
                statsMap["ftm"]?.add(seasonAverages.ftm)
                statsMap["fta"]?.add(seasonAverages.fta)
                statsMap["oreb"]?.add(seasonAverages.oreb)
                statsMap["reb"]?.add(seasonAverages.reb)
                statsMap["ast"]?.add(seasonAverages.ast)
                statsMap["stl"]?.add(seasonAverages.stl)
                statsMap["blk"]?.add(seasonAverages.blk)
                statsMap["turnover"]?.add(seasonAverages.turnover)
                statsMap["pf"]?.add(seasonAverages.pf)
                statsMap["pts"]?.add(seasonAverages.pts)
                statsMap["fg_pct"]?.add(seasonAverages.fg_pct * 100)
                statsMap["fg3_pct"]?.add(seasonAverages.fg3_pct * 100)
                statsMap["ft_pct"]?.add(seasonAverages.ft_pct * 100)
            }
            Log.d("Stats", statsMap.toString())
            binding.statsLayout.removeAllViews()
            statsMap.forEach { (key, list) ->
                val rowView = LayoutInflater.from(requireContext())
                    .inflate(R.layout.stats_table_row, fragmentContainer, false)
                val rowBinding = StatsTableRowBinding.bind(rowView)
                rowBinding.apply {
                    statLabel.text = key
                    firstStat.text = String.format("%.1f", list[0])
                    secondStat.text = String.format("%.1f", list[1])
                    thirdStat.text = String.format("%.1f", list[2])
                    fourthStat.text = String.format("%.1f", list[3])
                }
                when (list.indexOf(Collections.max(list))) {
                    0 -> rowBinding.firstStat.apply {
                        setBackgroundResource(R.drawable.statistics_highlight)
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.color_primary))
                    }
                    1 -> rowBinding.secondStat.apply {
                        setBackgroundResource(R.drawable.statistics_highlight)
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.color_primary))
                    }
                    2 -> rowBinding.thirdStat.apply {
                        setBackgroundResource(R.drawable.statistics_highlight)
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.color_primary))
                    }
                    3 -> rowBinding.fourthStat.apply {
                        setBackgroundResource(R.drawable.statistics_highlight)
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.color_primary))
                    }
                }
                rowView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
                binding.statsLayout.addView(rowView)
            }
        }

        super.onResume()
    }

    /*private fun setupSpinner() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1, //TODO popraviti izgled dropdown menija
            arrayOf(2021, 2020, 2019, 2018, 2017, 2016)
        )
        binding.seasonsMask.firstSeason.setAdapter(adapter)
        binding.seasonsMask.firstSeason.setSelection(0)
        binding.seasonsMask.firstSeason.setOnItemClickListener { adapterView, _, position, _ ->
            val season = adapterView?.getItemAtPosition(position) as Int
            sharedViewModel.getSeasonAveragesForPlayer(player.id, season)
        }
    }*/
}