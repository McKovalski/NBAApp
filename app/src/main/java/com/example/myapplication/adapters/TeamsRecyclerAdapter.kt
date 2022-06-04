package com.example.myapplication.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.activities.TeamDetailsActivity
import com.example.myapplication.database.NBAAppDatabase
import com.example.myapplication.databinding.SearchEmptyStateBinding
import com.example.myapplication.databinding.TeamItemViewBinding
import com.example.myapplication.fragments.ExploreFragment
import com.example.myapplication.helpers.TeamsHelper
import com.example.myapplication.models.Team
import kotlinx.coroutines.runBlocking
import java.io.Serializable

private const val EXTRA_TEAM = "team"
private const val EXTRA_TEAMS_IN_DIVISION = "teamsInDivision"
private const val EXTRA_IS_FAVOURITE: String = "isFavourite"
private const val TYPE_EMPTY_STATE = 0
private const val TYPE_TEAM = 1

@SuppressLint("NotifyDataSetChanged")
class TeamsRecyclerAdapter(
    private val context: Context,
    private val teamsList: MutableList<Team>,
    private val favouriteTeams: MutableList<Team>,
    private val fragment: ExploreFragment
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    fun updateFavourites(newFavourites: MutableList<Team>) {
        favouriteTeams.clear()
        favouriteTeams.addAll(newFavourites)
        notifyDataSetChanged()
    }

    fun updateTeams(newTeams: MutableList<Team>) {
        teamsList.clear()
        teamsList.addAll(newTeams)
        notifyDataSetChanged()
    }

    class TeamViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = TeamItemViewBinding.bind(view)
    }

    class TeamEmptyStateViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = SearchEmptyStateBinding.bind(view)
    }

    override fun getItemViewType(position: Int): Int {
        return if (teamsList.size == 0) TYPE_EMPTY_STATE else TYPE_TEAM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_EMPTY_STATE -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.search_empty_state, parent, false)
                TeamEmptyStateViewHolder(view)
            }
            TYPE_TEAM -> {
                val view =
                    LayoutInflater.from(context).inflate(R.layout.team_item_view, parent, false)
                TeamViewHolder(view)
            }
            else -> throw IllegalAccessException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TeamViewHolder) {
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
                    val lastPosition: Int
                    runBlocking {
                        lastPosition = NBAAppDatabase.getDatabase(context)?.teamsDao()
                            ?.getLastFavouriteTeamPosition() ?: 0
                    }
                    fragment.addFavouriteTeam(team.toFavouriteTeam(lastPosition + 1))
                }
                holder.binding.iconFavourite.apply { isSelected = !isSelected }
            }

            holder.itemView.setOnClickListener {
                val teamsInDivision = teamsList.filter {
                    it.division == team.division && it.name != team.name
                }
                val intent = Intent(context, TeamDetailsActivity::class.java)
                    .putExtra(EXTRA_TEAM, team)
                    .putExtra(EXTRA_TEAMS_IN_DIVISION, teamsInDivision as Serializable)
                    .putExtra(EXTRA_IS_FAVOURITE, holder.binding.iconFavourite.isSelected)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return if (teamsList.size > 0) teamsList.size else 1
    }
}