package com.example.myapplication.fragments

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.example.myapplication.R
import com.example.myapplication.activities.MatchDetailsActivity
import com.example.myapplication.databinding.FragmentMatchDetailsBinding
import com.example.myapplication.helpers.DateTimeHelper
import com.example.myapplication.helpers.StatHelper
import com.example.myapplication.helpers.TeamsHelper
import com.example.myapplication.models.Match
import com.example.myapplication.models.Stats
import com.example.myapplication.viewmodels.SharedViewModel
import kotlin.math.roundToInt

class MatchDetailsFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private var _binding: FragmentMatchDetailsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var match: Match
    private val gameStats = mutableListOf<Stats>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMatchDetailsBinding.inflate(inflater, container, false)

        match = (activity as MatchDetailsActivity).match
        sharedViewModel.getStatsForGame(match)

        setViews()

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setViews() {
        // Match Item Layout
        val (homeLogoId, homeColorId) = TeamsHelper().getLogoAndColor(match.home_team.name)
        binding.matchDetailStats.matchLayout.firstTeam.teamLogo.apply {
            image.setImageResource(homeLogoId)
            backgroundCard.setCardBackgroundColor(
                ContextCompat.getColorStateList(
                    requireContext(),
                    homeColorId
                )
            )
        }
        binding.matchDetailStats.matchLayout.firstTeam.teamNameAbbr.text =
            match.home_team.abbreviation
        val (awayLogoId, awayColorId) = TeamsHelper().getLogoAndColor(match.visitor_team.name)
        binding.matchDetailStats.matchLayout.secondTeam.teamLogo.apply {
            image.setImageResource(awayLogoId)
            backgroundCard.setCardBackgroundColor(
                ContextCompat.getColorStateList(
                    requireContext(),
                    awayColorId
                )
            )
        }
        binding.matchDetailStats.matchLayout.secondTeam.teamNameAbbr.text =
            match.visitor_team.abbreviation
        binding.matchDetailStats.matchLayout.firstTeamScore.text = match.home_team_score.toString()
        binding.matchDetailStats.matchLayout.secondTeamScore.text =
            match.visitor_team_score.toString()
        val firstTeamWon = match.home_team_score > match.visitor_team_score
        if (firstTeamWon) {
            binding.matchDetailStats.matchLayout.secondTeamScore.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.neutrals_n_lv_2
                )
            )
        } else {
            binding.matchDetailStats.matchLayout.firstTeamScore.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.neutrals_n_lv_2
                )
            )
        }
        binding.matchDetailStats.matchLayout.statusLabel.text = match.status
        binding.matchDetailStats.matchLayout.date.text =
            DateTimeHelper().getLongFormattedDate(match.date)

        // Stat Cells
        sharedViewModel.statsForGame.observe(viewLifecycleOwner) {
            gameStats.addAll(it)

            val homeColorTransparent = TeamsHelper().getTransparentColor(match.home_team.name)
            val awayColorTransparent = TeamsHelper().getTransparentColor(match.visitor_team.name)
            val homeStatsMap = StatHelper().getStats(gameStats, match.home_team.id)
            val awayStatsMap = StatHelper().getStats(gameStats, match.visitor_team.id)

            binding.matchDetailStats.fgStatCell.apply {
                statType.text = getString(R.string.fg_pct)
                firstTeamBar.progressTintList =
                    ContextCompat.getColorStateList(requireContext(), homeColorTransparent)
                secondTeamBar.progressTintList =
                    ContextCompat.getColorStateList(requireContext(), awayColorTransparent)
                firstTeamValue.text = homeStatsMap["fg_pct"].toString()
                firstTeamBar.progress =
                    StatHelper().getFieldGoalPercentage(gameStats, match.home_team.id)
                secondTeamValue.text = awayStatsMap["fg_pct"].toString()
                secondTeamBar.progress =
                    StatHelper().getFieldGoalPercentage(gameStats, match.visitor_team.id)
            }
            binding.matchDetailStats.fg3StatCell.apply {
                statType.text = getString(R.string.fg3_pct)
                firstTeamBar.progressTintList =
                    ContextCompat.getColorStateList(requireContext(), homeColorTransparent)
                secondTeamBar.progressTintList =
                    ContextCompat.getColorStateList(requireContext(), awayColorTransparent)
                firstTeamValue.text = homeStatsMap["fg3_pct"].toString()
                firstTeamBar.progress =
                    StatHelper().getThreePointPercentage(gameStats, match.home_team.id)
                secondTeamValue.text = awayStatsMap["fg3_pct"].toString()
                secondTeamBar.progress =
                    StatHelper().getThreePointPercentage(gameStats, match.visitor_team.id)
            }
            binding.matchDetailStats.rebStatCell.apply {
                val a = homeStatsMap["reb"]!!
                val b = awayStatsMap["reb"]!!
                statType.text = getString(R.string.oreb)
                firstTeamBar.progressTintList =
                    ContextCompat.getColorStateList(requireContext(), homeColorTransparent)
                secondTeamBar.progressTintList =
                    ContextCompat.getColorStateList(requireContext(), awayColorTransparent)
                firstTeamValue.text = a.toString()
                firstTeamBar.progress = ((a / (a + b).toFloat()) * 100).roundToInt()
                secondTeamValue.text = b.toString()
                secondTeamBar.progress = ((b / (a + b).toFloat()) * 100).roundToInt()
            }
            binding.matchDetailStats.astStatCell.apply {
                val a = homeStatsMap["ast"]!!
                val b = awayStatsMap["ast"]!!
                statType.text = getString(R.string.oreb)
                firstTeamBar.progressTintList =
                    ContextCompat.getColorStateList(requireContext(), homeColorTransparent)
                secondTeamBar.progressTintList =
                    ContextCompat.getColorStateList(requireContext(), awayColorTransparent)
                firstTeamValue.text = a.toString()
                firstTeamBar.progress = ((a / (a + b).toFloat()) * 100).roundToInt()
                secondTeamValue.text = b.toString()
                secondTeamBar.progress = ((b / (a + b).toFloat()) * 100).roundToInt()
            }
            binding.matchDetailStats.tovStatCell.apply {
                val a = homeStatsMap["tov"]!!
                val b = awayStatsMap["tov"]!!
                statType.text = getString(R.string.oreb)
                firstTeamBar.progressTintList =
                    ContextCompat.getColorStateList(requireContext(), homeColorTransparent)
                secondTeamBar.progressTintList =
                    ContextCompat.getColorStateList(requireContext(), awayColorTransparent)
                firstTeamValue.text = a.toString()
                firstTeamBar.progress = ((a / (a + b).toFloat()) * 100).roundToInt()
                secondTeamValue.text = b.toString()
                secondTeamBar.progress = ((b / (a + b).toFloat()) * 100).roundToInt()
            }
            binding.matchDetailStats.orebStatCell.apply {
                val a = homeStatsMap["oreb"]!!
                val b = awayStatsMap["oreb"]!!
                statType.text = getString(R.string.oreb)
                firstTeamBar.progressTintList =
                    ContextCompat.getColorStateList(requireContext(), homeColorTransparent)
                secondTeamBar.progressTintList =
                    ContextCompat.getColorStateList(requireContext(), awayColorTransparent)
                firstTeamValue.text = a.toString()
                firstTeamBar.progress = ((a / (a + b).toFloat()) * 100).roundToInt()
                secondTeamValue.text = b.toString()
                secondTeamBar.progress = ((b / (a + b).toFloat()) * 100).roundToInt()
            }
        }
    }
}