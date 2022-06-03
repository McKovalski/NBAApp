package com.example.myapplication.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.activities.MatchDetailsActivity
import com.example.myapplication.databinding.PlayerMatchCardWithHeaderBinding
import com.example.myapplication.fragments.PlayerMatchesFragment
import com.example.myapplication.helpers.DateTimeHelper
import com.example.myapplication.helpers.TeamsHelper
import com.example.myapplication.models.Match
import com.example.myapplication.models.Stats
import com.example.myapplication.models.Team

private const val EXTRA_MATCH = "match"

class StatPagingAdapter(
    private val context: Context,
    private val fragment: Fragment,
    diffCallback: DiffUtil.ItemCallback<Stats>
) : PagingDataAdapter<Stats, StatPagingAdapter.MatchViewHolder>(diffCallback) {

    private val allTeams = mutableListOf<Team>()

    @SuppressLint("NotifyDataSetChanged")
    fun updateTeams(teams: List<Team>) {
        allTeams.clear()
        allTeams.addAll(teams)
        notifyDataSetChanged()
    }

    class MatchViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = PlayerMatchCardWithHeaderBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val view =
            LayoutInflater.from(context)
                .inflate(R.layout.player_match_card_with_header, parent, false)
        return MatchViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        val stat = getItem(position)

        if (stat != null) {
            val match = stat.game
            val homeTeam = allTeams.first { t -> t.id == match.home_team_id }
            val awayTeam = allTeams.first { t -> t.id == match.visitor_team_id }

            if (position > 0) {
                val firstMonth = DateTimeHelper().getMonth(match.date)
                val secondMonth = DateTimeHelper().getMonth(getItem(position - 1)?.game?.date!!)
                if (firstMonth == secondMonth) {
                    holder.binding.date.visibility = View.GONE
                } else {
                    holder.binding.date.text = DateTimeHelper().getMonthAndYear(match.date)
                    holder.binding.date.visibility = View.VISIBLE
                }
            } else if (position == 0) {
                holder.binding.date.text = DateTimeHelper().getMonthAndYear(match.date)
                holder.binding.date.visibility = View.VISIBLE
            }

            val (homeLogoId, homeColorId) = TeamsHelper().getLogoAndColor(homeTeam.name)
            holder.binding.card.firstTeam.teamLogo.image.setImageResource(homeLogoId)
            holder.binding.card.firstTeam.teamLogo.backgroundCard.setCardBackgroundColor(
                ContextCompat.getColorStateList(
                    context,
                    homeColorId
                )
            )
            holder.binding.card.firstTeam.teamNameAbbr.text = homeTeam.abbreviation
            val (awayLogoId, awayColorId) = TeamsHelper().getLogoAndColor(awayTeam.name)
            holder.binding.card.secondTeam.teamLogo.image.setImageResource(awayLogoId)
            holder.binding.card.secondTeam.teamLogo.backgroundCard.setCardBackgroundColor(
                ContextCompat.getColorStateList(
                    context,
                    awayColorId
                )
            )
            holder.binding.card.secondTeam.teamNameAbbr.text = awayTeam.abbreviation

            val firstTeamWon = match.home_team_score > match.visitor_team_score
            if (firstTeamWon) {
                holder.binding.card.firstTeamScore.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.neutrals_n_lv_1
                    )
                )
                holder.binding.card.secondTeamScore.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.neutrals_n_lv_2
                    )
                )
            } else {
                holder.binding.card.firstTeamScore.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.neutrals_n_lv_2
                    )
                )
                holder.binding.card.secondTeamScore.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.neutrals_n_lv_1
                    )
                )
            }
            holder.binding.card.firstTeamScore.text = match.home_team_score.toString()
            holder.binding.card.secondTeamScore.text = match.visitor_team_score.toString()

            holder.binding.card.date.text = DateTimeHelper().getDayMonthYear(match.date)

            holder.binding.card.statClickSpace.setOnClickListener {
                (fragment as PlayerMatchesFragment).showStatsBottomDialog(stat)
            }

            holder.itemView.setOnClickListener {
                val intent = Intent(context, MatchDetailsActivity::class.java)
                    .putExtra(EXTRA_MATCH, match.toMatch(homeTeam, awayTeam))
                context.startActivity(intent)
            }
        }
    }
}

object StatDiffCallback : DiffUtil.ItemCallback<Stats>() {
    override fun areItemsTheSame(oldItem: Stats, newItem: Stats): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Stats, newItem: Stats): Boolean {
        return oldItem == newItem
    }
}