package com.example.myapplication.adapters

import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.myapplication.R
import com.example.myapplication.activities.MatchDetailsActivity
import com.example.myapplication.databinding.MatchCardWithHeaderBinding
import com.example.myapplication.databinding.MatchItemCardBinding
import com.example.myapplication.helpers.DateTimeHelper
import com.example.myapplication.helpers.TeamsHelper
import com.example.myapplication.models.Match
import com.example.myapplication.models.Player

private const val EXTRA_MATCH = "match"

class MatchPagingAdapter(
    private val context: Context,
    diffCallback: DiffUtil.ItemCallback<Match>
) : PagingDataAdapter<Match, MatchPagingAdapter.MatchViewHolder>(diffCallback) {

    class MatchViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = MatchCardWithHeaderBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.match_card_with_header, parent, false)
        return MatchViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        val match = getItem(position)

        if (match != null) {
            if (position > 0) {
                val firstMonth = DateTimeHelper().getMonth(match.date)
                val secondMonth = DateTimeHelper().getMonth(getItem(position - 1)?.date!!)
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
            val (homeLogoId, homeColorId) = TeamsHelper().getLogoAndColor(match.home_team.name)
            holder.binding.card.firstTeam.teamLogo.image.setImageResource(homeLogoId)
            holder.binding.card.firstTeam.teamLogo.backgroundCard.setCardBackgroundColor(
                ContextCompat.getColorStateList(
                    context,
                    homeColorId
                )
            )
            holder.binding.card.firstTeam.teamNameAbbr.text = match.home_team.abbreviation
            val (awayLogoId, awayColorId) = TeamsHelper().getLogoAndColor(match.visitor_team.name)
            holder.binding.card.secondTeam.teamLogo.image.setImageResource(awayLogoId)
            holder.binding.card.secondTeam.teamLogo.backgroundCard.setCardBackgroundColor(
                ContextCompat.getColorStateList(
                    context,
                    awayColorId
                )
            )
            holder.binding.card.secondTeam.teamNameAbbr.text = match.visitor_team.abbreviation

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
            holder.binding.card.statusLabel.text = match.status

            holder.binding.card.date.text = DateTimeHelper().getLongFormattedDate(match.date)

            holder.itemView.setOnClickListener {
                val intent = Intent(context, MatchDetailsActivity::class.java)
                    .putExtra(EXTRA_MATCH, match)
                context.startActivity(intent)
            }
        }
    }
}

object MatchDiffCallback : DiffUtil.ItemCallback<Match>() {
    override fun areItemsTheSame(oldItem: Match, newItem: Match): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Match, newItem: Match): Boolean {
        return oldItem == newItem
    }

}