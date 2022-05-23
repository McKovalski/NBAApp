package com.example.myapplication.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.activities.TeamDetailsActivity
import com.example.myapplication.databinding.TeamItemViewBinding
import com.example.myapplication.fragments.ExploreFragment
import com.example.myapplication.helpers.TeamsHelper
import com.example.myapplication.models.Team

private const val EXTRA_TEAM = "team"

class TeamsRecyclerAdapter(
    private val context: Context,
    private val teamsList: MutableList<Team>,
    private val favouriteTeams: MutableList<Team>,
    private val fragment: ExploreFragment
) : RecyclerView.Adapter<TeamsRecyclerAdapter.TeamViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun updateFavourites(newFavourites: MutableList<Team>) {
        favouriteTeams.clear()
        favouriteTeams.addAll(newFavourites)
        notifyDataSetChanged()
    }

    class TeamViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = TeamItemViewBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.team_item_view, parent, false)
        return TeamViewHolder(view)
    }

    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        val team = teamsList[position]

        val (logoId, colorId) = TeamsHelper().getLogoAndColor(team.name)
        holder.binding.name.text = team.name
        holder.binding.logo.image.setImageResource(logoId)
        holder.binding.logo.backgroundCard.setCardBackgroundColor(
            ContextCompat.getColorStateList(
                context,
                colorId
            )
        )
        holder.binding.iconFavourite.isSelected = team in favouriteTeams

        holder.binding.iconFavourite.setOnClickListener {
            if (holder.binding.iconFavourite.isSelected) {
                favouriteTeams.remove(team)
                fragment.removeFavouriteTeam(team.id)
            } else {
                favouriteTeams.add(team)
                val newPosition = fragment.getLastFavouriteTeamPosition() + 1
                fragment.addFavouriteTeam(team.toFavouriteTeam(newPosition))
            }
            holder.binding.iconFavourite.apply {
                isSelected = !isSelected
            }
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, TeamDetailsActivity::class.java)
                .putExtra(EXTRA_TEAM, team)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return teamsList.size
    }
}