package com.example.myapplication.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.StatsTableRowBinding
import com.example.myapplication.helpers.StatDisplayNameHelper

class PlayerStatsRecyclerAdapter(
    private val context: Context,
    private val statsMap: LinkedHashMap<String, Float>
) : RecyclerView.Adapter<PlayerStatsRecyclerAdapter.StatViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun updateStats(newStatsMap: LinkedHashMap<String, Float>) {
        statsMap.putAll(newStatsMap)
        notifyDataSetChanged()
    }

    class StatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = StatsTableRowBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.stats_table_row, parent, false)
        return StatViewHolder(view)
    }

    override fun onBindViewHolder(holder: StatViewHolder, position: Int) {
        val statName = statsMap.keys.toList()[position]
        val statValue = statsMap[statName]

        val statDisplayName = StatDisplayNameHelper().getDisplayName(statName)
        holder.binding.statLabel.text = statDisplayName
        holder.binding.statValue.text =
            String.format("%.1f", statValue) //TODO mozda ce trebati mijenjati za player match
    }

    override fun getItemCount(): Int {
        return statsMap.size
    }
}