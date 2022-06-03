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
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.activities.MatchDetailsActivity
import com.example.myapplication.databinding.TeamMatchWithHeaderBinding
import com.example.myapplication.helpers.DateTimeHelper
import com.example.myapplication.helpers.TeamsHelper
import com.example.myapplication.models.Match
import com.example.myapplication.models.Team

private const val EXTRA_MATCH = "match"

@SuppressLint("NotifyDataSetChanged")
class TeamMatchPagingAdapter(
    private val context: Context,
    diffCallback: DiffUtil.ItemCallback<Match>
) : PagingDataAdapter<Match, TeamMatchPagingAdapter.TeamMatchViewHolder>(diffCallback) {

    private lateinit var team: Team

    fun setTeam(t: Team) {
        team = t
        notifyDataSetChanged()
    }

    class TeamMatchViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = TeamMatchWithHeaderBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamMatchViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.team_match_with_header, parent, false)
        return TeamMatchViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: TeamMatchViewHolder, position: Int) {
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

            holder.binding.card.dateDetail.day.text = DateTimeHelper().getDayOfWeek(match.date)
            holder.binding.card.dateDetail.date.text = DateTimeHelper().getDayAndMonth(match.date)

            var isHomeTeam = false
            if (match.home_team.id == team.id) {
                isHomeTeam = true
            }
            if (isHomeTeam) {
                val otherTeam = match.visitor_team
                val (logoId, _) = TeamsHelper().getLogoAndColor(otherTeam.name)

                holder.binding.card.opponentDetail.apply {
                    homeOrAwayLabel.text = context.getString(R.string.vs)
                    opponentAbbr.text = otherTeam.abbreviation
                    opponentLogo.setImageResource(logoId)
                }
                holder.binding.card.scoreDetail.apply {
                    firstTeamScore.text = match.home_team_score.toString()
                    secondTeamScore.text = match.visitor_team_score.toString()
                    if (match.home_team_score > match.visitor_team_score) {
                        lossOrWin.text = "W"
                        lossOrWin.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.status_success
                            )
                        )
                    } else {
                        lossOrWin.text = "L"
                        lossOrWin.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.status_error
                            )
                        )
                    }
                }
            } else {
                val otherTeam = match.home_team
                val (logoId, _) = TeamsHelper().getLogoAndColor(otherTeam.name)

                holder.binding.card.opponentDetail.apply {
                    homeOrAwayLabel.text = "@"
                    opponentAbbr.text = otherTeam.abbreviation
                    opponentLogo.setImageResource(logoId)
                }
                holder.binding.card.scoreDetail.apply {
                    firstTeamScore.text = match.home_team_score.toString()
                    secondTeamScore.text = match.visitor_team_score.toString()
                    if (match.home_team_score < match.visitor_team_score) {
                        lossOrWin.text = "W"
                        lossOrWin.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.status_success
                            )
                        )
                    } else {
                        lossOrWin.text = "L"
                        lossOrWin.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.status_error
                            )
                        )
                    }
                }
            }

            holder.itemView.setOnClickListener {
                val intent = Intent(context, MatchDetailsActivity::class.java)
                    .putExtra(EXTRA_MATCH, match)
                context.startActivity(intent)
            }
        }
    }
}