package com.example.myapplication.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.myapplication.R
import com.example.myapplication.activities.TeamDetailsActivity
import com.example.myapplication.databinding.FavouriteTeamViewBinding
import com.example.myapplication.fragments.FavouritesFragment
import com.example.myapplication.helpers.TeamsHelper
import com.example.myapplication.models.Team
import java.io.Serializable
import java.util.*

private const val EXTRA_TEAM = "team"
private const val EXTRA_TEAMS_IN_DIVISION = "teamsInDivision"
private const val EXTRA_IS_FAVOURITE: String = "isFavourite"
private const val TYPE_EMPTY_STATE = 0
private const val TYPE_TEAM = 1

@SuppressLint("NotifyDataSetChanged")
class FavouriteTeamsRecyclerAdapter(
    private val context: Context,
    private val favouriteTeams: MutableList<Team>,
    private val fragment: FavouritesFragment
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var showReorder: Boolean = false

    fun switchReorder() {
        showReorder = !showReorder
        notifyDataSetChanged()
    }

    fun updateList(newFavourites: MutableList<Team>) {
        favouriteTeams.clear()
        favouriteTeams.addAll(newFavourites)
        notifyDataSetChanged()
    }

    class TeamViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = FavouriteTeamViewBinding.bind(view)
    }

    override fun getItemViewType(position: Int): Int {
        return if (favouriteTeams.size == 0) TYPE_EMPTY_STATE else TYPE_TEAM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_EMPTY_STATE -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.search_empty_state, parent, false)
                TeamsRecyclerAdapter.TeamEmptyStateViewHolder(view)
            }
            TYPE_TEAM -> {
                val view =
                    LayoutInflater.from(context)
                        .inflate(R.layout.favourite_team_view, parent, false)
                TeamViewHolder(view)
            }
            else -> throw IllegalAccessException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TeamsRecyclerAdapter.TeamEmptyStateViewHolder) {
            holder.binding.image.load(R.drawable.ic_sad_star)
            holder.binding.topText.text = context.getString(R.string.no_favourite_teams)
            holder.binding.bottomText.text =
                context.getString(R.string.add_favourite_teams_in_explore)
        } else if (holder is TeamViewHolder) {
            val team = favouriteTeams[position]

            val (logoId, colorId) = TeamsHelper().getLogoAndColor(team.name)
            holder.binding.card.name.text = team.name
            holder.binding.card.logo.image.setImageResource(logoId)
            holder.binding.card.logo.backgroundCard.setCardBackgroundColor(
                ContextCompat.getColorStateList(
                    context,
                    colorId
                )
            )

            holder.binding.card.iconFavourite.isSelected = true
            holder.binding.card.iconFavourite.setOnClickListener {
                holder.binding.card.iconFavourite.isSelected = false
                fragment.removeFavouriteTeam(team.id)
                notifyItemRemoved(position)
                fragment.showRemovedFavouriteSnackbar(team.full_name)
            }

            if (showReorder) {
                holder.binding.reorderIcon.visibility = View.VISIBLE
            } else {
                holder.binding.reorderIcon.visibility = View.GONE
            }

            holder.itemView.setOnClickListener {
                val teamsInDivision = fragment.getAllTeams()?.filter {
                    it.division == team.division && it.name != team.name
                }
                val intent = Intent(context, TeamDetailsActivity::class.java)
                    .putExtra(EXTRA_TEAM, team)
                    .putExtra(EXTRA_TEAMS_IN_DIVISION, teamsInDivision as Serializable)
                    .putExtra(EXTRA_IS_FAVOURITE, true)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return if (favouriteTeams.size > 0) favouriteTeams.size else 1
    }

    fun swapItems(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(favouriteTeams, i, i + 1)
            }
        } else {
            for (i in toPosition until fromPosition) {
                Collections.swap(favouriteTeams, i, i + 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }
}