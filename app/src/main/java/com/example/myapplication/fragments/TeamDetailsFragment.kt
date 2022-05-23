package com.example.myapplication.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.example.myapplication.R
import com.example.myapplication.activities.TeamDetailsActivity
import com.example.myapplication.databinding.FragmentTeamDetailsBinding
import com.example.myapplication.helpers.TeamsHelper
import com.example.myapplication.models.Team
import com.example.myapplication.viewmodels.SharedViewModel

class TeamDetailsFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private var _binding: FragmentTeamDetailsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTeamDetailsBinding.inflate(inflater, container, false)

        val team = (activity as TeamDetailsActivity).team
        val teamsInDivision = (activity as TeamDetailsActivity).teamsInDivision
        setViews(team, teamsInDivision)

        return binding.root
    }

    private fun setViews(team: Team, teamsInDivision: List<Team>) {
        // Team Details Card
        binding.teamDetailsCard.teamNameAbbr.text = team.abbreviation
        binding.teamDetailsCard.teamName.text = team.full_name
        binding.teamDetailsCard.teamCity.text = team.city
        binding.teamDetailsCard.bottomSubDetails.firstDetail.type.text =
            getString(R.string.conference)
        binding.teamDetailsCard.bottomSubDetails.firstDetail.value.text =
            getString(TeamsHelper().getConferenceName(team.conference))
        binding.teamDetailsCard.bottomSubDetails.secondDetail.type.text =
            getString(R.string.division)
        binding.teamDetailsCard.bottomSubDetails.secondDetail.value.text =
            getString(TeamsHelper().getDivisionName(team.division))
        val (logoId, colorId) = TeamsHelper().getLogoAndColor(team.name)
        binding.teamDetailsCard.teamLogo.image.setImageResource(logoId)
        binding.teamDetailsCard.teamLogo.backgroundCard.setCardBackgroundColor(
            ContextCompat.getColorStateList(
                requireContext(),
                colorId
            )
        )

        // Map
        binding.mapLayout.arenaLocationCity.text = team.city

        // Teams in conference
        teamsInDivision.forEachIndexed { i, t ->
            val (logo, color) = TeamsHelper().getLogoAndColor(t.name)
            when (i) {
                0 -> binding.firstTeam.apply {
                    teamNameAbbr.text = t.abbreviation
                    teamLogo.image.setImageResource(logo)
                    teamLogo.backgroundCard.setCardBackgroundColor(
                        ContextCompat.getColorStateList(
                            requireContext(),
                            color
                        )
                    )
                }
                1 -> binding.secondTeam.apply {
                    teamNameAbbr.text = t.abbreviation
                    teamLogo.image.setImageResource(logo)
                    teamLogo.backgroundCard.setCardBackgroundColor(
                        ContextCompat.getColorStateList(
                            requireContext(),
                            color
                        )
                    )
                }
                2 -> binding.thirdTeam.apply {
                    teamNameAbbr.text = t.abbreviation
                    teamLogo.image.setImageResource(logo)
                    teamLogo.backgroundCard.setCardBackgroundColor(
                        ContextCompat.getColorStateList(
                            requireContext(),
                            color
                        )
                    )
                }
                else -> binding.fourthTeam.apply {
                    teamNameAbbr.text = t.abbreviation
                    teamLogo.image.setImageResource(logo)
                    teamLogo.backgroundCard.setCardBackgroundColor(
                        ContextCompat.getColorStateList(
                            requireContext(),
                            color
                        )
                    )
                }
            }
        }
    }
}