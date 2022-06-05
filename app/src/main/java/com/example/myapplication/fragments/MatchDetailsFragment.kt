package com.example.myapplication.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.activities.MatchDetailsActivity
import com.example.myapplication.adapters.HighlightsRecyclerAdapter
import com.example.myapplication.databinding.AddImageBottomSheetLayoutBinding
import com.example.myapplication.databinding.FragmentMatchDetailsBinding
import com.example.myapplication.helpers.DateTimeHelper
import com.example.myapplication.helpers.StatHelper
import com.example.myapplication.helpers.TeamsHelper
import com.example.myapplication.helpers.YoutubeVideoHelper
import com.example.myapplication.models.Highlight
import com.example.myapplication.models.Match
import com.example.myapplication.models.Stats
import com.example.myapplication.viewmodels.SharedViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlin.math.roundToInt

class MatchDetailsFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private var _binding: FragmentMatchDetailsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val highlightsAdapter by lazy {
        HighlightsRecyclerAdapter(
            requireContext(),
            mutableListOf(),
            this
        )
    }

    private lateinit var match: Match
    private val gameStats = mutableListOf<Stats>()
    private val highlights = mutableListOf<Highlight>()
    private var isEditing = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMatchDetailsBinding.inflate(inflater, container, false)

        match = (activity as MatchDetailsActivity).match
        Log.d("matchId", match.id.toString())

        binding.recyclerHighlights.adapter = highlightsAdapter
        binding.recyclerHighlights.layoutManager = LinearLayoutManager(requireContext())

        binding.iconEdit.isVisible = false
        binding.iconAddHighlight.isVisible = false

        sharedViewModel.getStatsForGame(match)
        sharedViewModel.eventHighlights.observe(viewLifecycleOwner) {
            highlights.clear()
            highlights.addAll(it)
            highlightsAdapter.updateHighlights(highlights)

            if (highlights.isEmpty()) {
                binding.iconEdit.isVisible = false
                binding.iconAddHighlight.isVisible = false
            } else {
                binding.iconEdit.isVisible = true
                binding.iconAddHighlight.isVisible = true
            }
        }

        binding.iconAddHighlight.setOnClickListener {
            setBottomSheetDialog()
        }

        binding.iconEdit.setOnClickListener {
            if (!isEditing) {
                isEditing = true
                binding.iconEdit.setImageResource(R.drawable.ic_close)
            } else {
                isEditing = false
                binding.iconEdit.setImageResource(R.drawable.ic_edit)
            }
            highlightsAdapter.switchReorder()
        }

        setViews()

        return binding.root
    }

    override fun onResume() {
        sharedViewModel.getEventHighlights(match.id)
        super.onResume()
    }

    // Reusing the bottom sheet dialog layout from Player Details Fragment
    // and renaming the photo title to video title
    fun setBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.add_image_bottom_sheet_layout, null)
        val bottomSheetBinding = AddImageBottomSheetLayoutBinding.bind(view)

        bottomSheetBinding.title.text = getString(R.string.add_youtube_video)
        bottomSheetBinding.urlInputLayout.helperText = getString(R.string.paste_video_url)
        bottomSheetBinding.captionInputLayout.setHint(getString(R.string.video_title))
        bottomSheetBinding.captionInputLayout.helperText = getString(R.string.video_helper_text)

        bottomSheetBinding.buttonCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetBinding.buttonAdd.setOnClickListener {
            val url = bottomSheetBinding.urlInputEditText.text.toString()
            val caption = bottomSheetBinding.captionInputEditText.text.toString()
            if (!YoutubeVideoHelper().isValidUrl(url)) {
                bottomSheetBinding.urlInputLayout.error = getString(R.string.invalid_url)
            } else if (caption.isEmpty()) {
                bottomSheetBinding.urlInputLayout.error = ""
                bottomSheetBinding.captionInputLayout.error =
                    getString(R.string.caption_cannot_be_blank)
            } else {
                sharedViewModel.postHighlight(match.id, caption, url, startTimestamp = 0)
                bottomSheetBinding.urlInputEditText.text?.clear()
                bottomSheetBinding.urlInputLayout.error = ""
                bottomSheetBinding.urlInputLayout.clearFocus()
                bottomSheetBinding.captionInputEditText.text?.clear()
                bottomSheetBinding.captionInputLayout.error = ""
                bottomSheetBinding.captionInputLayout.clearFocus()
                bottomSheetDialog.dismiss()

                sharedViewModel.getEventHighlights(match.id)
            }
        }
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }

    fun deleteHighlight(id: Int) {
        sharedViewModel.deleteHighlightById(id)
        highlights.removeAll { h -> h.id == id }
        highlightsAdapter.updateHighlights(highlights)

        if (highlights.isEmpty()) {
            binding.iconEdit.setImageResource(R.drawable.ic_edit)
            binding.iconEdit.isVisible = false
            binding.iconAddHighlight.isVisible = false
        }
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
                statType.text = getString(R.string.reb)
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
                statType.text = getString(R.string.ast)
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
                statType.text = getString(R.string.tov)
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