package com.example.myapplication.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.FilterItemViewBinding
import com.example.myapplication.fragments.FilterType
import com.example.myapplication.fragments.SeasonsFragment
import com.example.myapplication.fragments.TeamMatchesFragment

@SuppressLint("NotifyDataSetChanged")
class FilterRecyclerAdapter(
    private val context: Context,
    private val filters: MutableList<String>,
    private val filterTypes: MutableList<FilterType>,
    private val fragment: Fragment,
    private val fragmentType: String
) : RecyclerView.Adapter<FilterRecyclerAdapter.FilterViewHolder>() {

    fun updateFilters(newFilters: MutableList<String>) {
        filters.clear()
        filters.addAll(newFilters)
        notifyDataSetChanged()
    }

    fun updateFilterTypes(newFilterTypes: MutableList<FilterType>) {
        filterTypes.clear()
        filterTypes.addAll(newFilterTypes)
        notifyDataSetChanged()
    }

    class FilterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = FilterItemViewBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.filter_item_view, parent, false)
        return FilterViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        val filter = filters[position]
        val filterType = filterTypes[position]

        when (filterType) {
            FilterType.SEASON -> holder.binding.text.text = "$filter-${filter.toInt() + 1}"
            FilterType.TEAM -> holder.binding.text.text = filter
        }

        holder.itemView.setOnClickListener {
            when (fragmentType) {
                "Seasons" -> (fragment as SeasonsFragment).removeFilter(filterType)
                "TeamMatches" -> (fragment as TeamMatchesFragment).removeFilter(filterType)
            }
        }
    }

    override fun getItemCount(): Int {
        return filters.size
    }
}