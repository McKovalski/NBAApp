package com.example.myapplication.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import coil.load
import com.example.myapplication.R
import com.example.myapplication.activities.MatchDetailsActivity
import com.example.myapplication.databinding.FragmentMatchTopPlayersBinding
import com.example.myapplication.helpers.StatHelper
import com.example.myapplication.models.Match
import com.example.myapplication.models.PlayerImage
import com.example.myapplication.models.Stats
import com.example.myapplication.models.Team
import com.example.myapplication.viewmodels.SharedViewModel

class MatchTopPlayersFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private var _binding: FragmentMatchTopPlayersBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var match: Match
    private val gameStats = mutableListOf<Stats>()
    private val favouriteImages = mutableListOf<PlayerImage>()
    private val allTeams = mutableListOf<Team>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMatchTopPlayersBinding.inflate(inflater, container, false)

        match = (activity as MatchDetailsActivity).match
        sharedViewModel.getAllFavouriteImages(requireContext())
        sharedViewModel.getAllTeams(requireContext())

        setViews()

        return binding.root
    }

    private fun setViews() {
        sharedViewModel.statsForGame.observe(viewLifecycleOwner) { statsList ->
            gameStats.addAll(statsList)
            sharedViewModel.allFavouriteImages.observe(viewLifecycleOwner) { images ->
                favouriteImages.addAll(images)
                sharedViewModel.allTeams.observe(viewLifecycleOwner) { teams ->
                    allTeams.addAll(teams)
                    val topStatsMap = StatHelper().getTopPlayers(gameStats, allTeams)

                    // Points
                    val topPoints = topStatsMap["pts"]!!.reversed().take(3)
                    binding.statsPoints.apply {
                        statTitle.text = getString(R.string.points)
                        firstPlayerCell.apply {
                            index.text = "1"
                            name.text = topPoints[0].first.fullName()
                            club.text = topPoints[0].first.team.abbreviation
                            statValue.text = topPoints[0].second.toString()
                            var imageResource: Any = R.drawable.ic_player_1_small
                            for (image in favouriteImages) {
                                if (image.playerId == topPoints[0].first.id) {
                                    imageResource = image.imageUrl
                                    break
                                }
                            }
                            image.load(imageResource)
                        }
                        secondPlayerCell.apply {
                            index.text = "2"
                            name.text = topPoints[1].first.fullName()
                            club.text = topPoints[1].first.team.abbreviation
                            statValue.text = topPoints[1].second.toString()
                            var imageResource: Any = R.drawable.ic_player_2_small
                            for (image in favouriteImages) {
                                if (image.playerId == topPoints[1].first.id) {
                                    imageResource = image.imageUrl
                                    break
                                }
                            }
                            image.load(imageResource)
                        }
                        thirdPlayerCell.apply {
                            index.text = "3"
                            name.text = topPoints[2].first.fullName()
                            club.text = topPoints[2].first.team.abbreviation
                            statValue.text = topPoints[2].second.toString()
                            var imageResource: Any = R.drawable.ic_player_3_small
                            for (image in favouriteImages) {
                                if (image.playerId == topPoints[2].first.id) {
                                    imageResource = image.imageUrl
                                    break
                                }
                            }
                            image.load(imageResource)
                        }
                    }
                    // Field Goals Made
                    val topFgm = topStatsMap["fgm"]!!.reversed().take(3)
                    binding.statsFieldGoals.apply {
                        statTitle.text = getString(R.string.field_goals_made)
                        firstPlayerCell.apply {
                            index.text = "1"
                            name.text = topFgm[0].first.fullName()
                            club.text = topFgm[0].first.team.abbreviation
                            statValue.text = topFgm[0].second.toString()
                            var imageResource: Any = R.drawable.ic_player_1_small
                            for (image in favouriteImages) {
                                if (image.playerId == topFgm[0].first.id) {
                                    imageResource = image.imageUrl
                                    break
                                }
                            }
                            image.load(imageResource)
                        }
                        secondPlayerCell.apply {
                            index.text = "2"
                            name.text = topFgm[1].first.fullName()
                            club.text = topFgm[1].first.team.abbreviation
                            statValue.text = topFgm[1].second.toString()
                            var imageResource: Any = R.drawable.ic_player_2_small
                            for (image in favouriteImages) {
                                if (image.playerId == topFgm[1].first.id) {
                                    imageResource = image.imageUrl
                                    break
                                }
                            }
                            image.load(imageResource)
                        }
                        thirdPlayerCell.apply {
                            index.text = "3"
                            name.text = topFgm[2].first.fullName()
                            club.text = topFgm[2].first.team.abbreviation
                            statValue.text = topFgm[2].second.toString()
                            var imageResource: Any = R.drawable.ic_player_3_small
                            for (image in favouriteImages) {
                                if (image.playerId == topFgm[2].first.id) {
                                    imageResource = image.imageUrl
                                    break
                                }
                            }
                            image.load(imageResource)
                        }
                    }
                    // 3-pointers Made
                    val top3pm = topStatsMap["fg3m"]!!.reversed().take(3)
                    binding.stats3Pointers.apply {
                        statTitle.text = getString(R.string.field_goal_3_pts_made)
                        firstPlayerCell.apply {
                            index.text = "1"
                            name.text = top3pm[0].first.fullName()
                            club.text = top3pm[0].first.team.abbreviation
                            statValue.text = top3pm[0].second.toString()
                            var imageResource: Any = R.drawable.ic_player_1_small
                            for (image in favouriteImages) {
                                if (image.playerId == top3pm[0].first.id) {
                                    imageResource = image.imageUrl
                                    break
                                }
                            }
                            image.load(imageResource)
                        }
                        secondPlayerCell.apply {
                            index.text = "2"
                            name.text = top3pm[1].first.fullName()
                            club.text = top3pm[1].first.team.abbreviation
                            statValue.text = top3pm[1].second.toString()
                            var imageResource: Any = R.drawable.ic_player_2_small
                            for (image in favouriteImages) {
                                if (image.playerId == top3pm[1].first.id) {
                                    imageResource = image.imageUrl
                                    break
                                }
                            }
                            image.load(imageResource)
                        }
                        thirdPlayerCell.apply {
                            index.text = "3"
                            name.text = top3pm[2].first.fullName()
                            club.text = top3pm[2].first.team.abbreviation
                            statValue.text = top3pm[2].second.toString()
                            var imageResource: Any = R.drawable.ic_player_3_small
                            for (image in favouriteImages) {
                                if (image.playerId == top3pm[2].first.id) {
                                    imageResource = image.imageUrl
                                    break
                                }
                            }
                            image.load(imageResource)
                        }
                    }
                    // Offensive Rebounds
                    val topOreb = topStatsMap["oreb"]!!.reversed().take(3)
                    binding.statsOffRebounds.apply {
                        statTitle.text = getString(R.string.offensive_rebounds)
                        firstPlayerCell.apply {
                            index.text = "1"
                            name.text = topOreb[0].first.fullName()
                            club.text = topOreb[0].first.team.abbreviation
                            statValue.text = topOreb[0].second.toString()
                            var imageResource: Any = R.drawable.ic_player_1_small
                            for (image in favouriteImages) {
                                if (image.playerId == topOreb[0].first.id) {
                                    imageResource = image.imageUrl
                                    break
                                }
                            }
                            image.load(imageResource)
                        }
                        secondPlayerCell.apply {
                            index.text = "2"
                            name.text = topOreb[1].first.fullName()
                            club.text = topOreb[1].first.team.abbreviation
                            statValue.text = topOreb[1].second.toString()
                            var imageResource: Any = R.drawable.ic_player_2_small
                            for (image in favouriteImages) {
                                if (image.playerId == topOreb[1].first.id) {
                                    imageResource = image.imageUrl
                                    break
                                }
                            }
                            image.load(imageResource)
                        }
                        thirdPlayerCell.apply {
                            index.text = "3"
                            name.text = topOreb[2].first.fullName()
                            club.text = topOreb[2].first.team.abbreviation
                            statValue.text = topOreb[2].second.toString()
                            var imageResource: Any = R.drawable.ic_player_3_small
                            for (image in favouriteImages) {
                                if (image.playerId == topOreb[2].first.id) {
                                    imageResource = image.imageUrl
                                    break
                                }
                            }
                            image.load(imageResource)
                        }
                    }
                    // Rebounds
                    val topReb = topStatsMap["reb"]!!.reversed().take(3)
                    binding.statsRebounds.apply {
                        statTitle.text = getString(R.string.rebounds)
                        firstPlayerCell.apply {
                            index.text = "1"
                            name.text = topReb[0].first.fullName()
                            club.text = topReb[0].first.team.abbreviation
                            statValue.text = topReb[0].second.toString()
                            var imageResource: Any = R.drawable.ic_player_1_small
                            for (image in favouriteImages) {
                                if (image.playerId == topReb[0].first.id) {
                                    imageResource = image.imageUrl
                                    break
                                }
                            }
                            image.load(imageResource)
                        }
                        secondPlayerCell.apply {
                            index.text = "2"
                            name.text = topReb[1].first.fullName()
                            club.text = topReb[1].first.team.abbreviation
                            statValue.text = topReb[1].second.toString()
                            var imageResource: Any = R.drawable.ic_player_2_small
                            for (image in favouriteImages) {
                                if (image.playerId == topReb[1].first.id) {
                                    imageResource = image.imageUrl
                                    break
                                }
                            }
                            image.load(imageResource)
                        }
                        thirdPlayerCell.apply {
                            index.text = "3"
                            name.text = topReb[2].first.fullName()
                            club.text = topReb[2].first.team.abbreviation
                            statValue.text = topReb[2].second.toString()
                            var imageResource: Any = R.drawable.ic_player_3_small
                            for (image in favouriteImages) {
                                if (image.playerId == topReb[2].first.id) {
                                    imageResource = image.imageUrl
                                    break
                                }
                            }
                            image.load(imageResource)
                        }
                    }
                    // Assists
                    val topAst = topStatsMap["ast"]!!.reversed().take(3)
                    binding.statsAssists.apply {
                        statTitle.text = getString(R.string.assists)
                        firstPlayerCell.apply {
                            index.text = "1"
                            name.text = topAst[0].first.fullName()
                            club.text = topAst[0].first.team.abbreviation
                            statValue.text = topAst[0].second.toString()
                            var imageResource: Any = R.drawable.ic_player_1_small
                            for (image in favouriteImages) {
                                if (image.playerId == topAst[0].first.id) {
                                    imageResource = image.imageUrl
                                    break
                                }
                            }
                            image.load(imageResource)
                        }
                        secondPlayerCell.apply {
                            index.text = "2"
                            name.text = topAst[1].first.fullName()
                            club.text = topAst[1].first.team.abbreviation
                            statValue.text = topAst[1].second.toString()
                            var imageResource: Any = R.drawable.ic_player_2_small
                            for (image in favouriteImages) {
                                if (image.playerId == topAst[1].first.id) {
                                    imageResource = image.imageUrl
                                    break
                                }
                            }
                            image.load(imageResource)
                        }
                        thirdPlayerCell.apply {
                            index.text = "3"
                            name.text = topAst[2].first.fullName()
                            club.text = topAst[2].first.team.abbreviation
                            statValue.text = topAst[2].second.toString()
                            var imageResource: Any = R.drawable.ic_player_3_small
                            for (image in favouriteImages) {
                                if (image.playerId == topAst[2].first.id) {
                                    imageResource = image.imageUrl
                                    break
                                }
                            }
                            image.load(imageResource)
                        }
                    }
                    // Turnovers
                    val topTov = topStatsMap["tov"]!!.reversed().take(3)
                    binding.statsTurnovers.apply {
                        statTitle.text = getString(R.string.turnovers)
                        firstPlayerCell.apply {
                            index.text = "1"
                            name.text = topTov[0].first.fullName()
                            club.text = topTov[0].first.team.abbreviation
                            statValue.text = topTov[0].second.toString()
                            var imageResource: Any = R.drawable.ic_player_1_small
                            for (image in favouriteImages) {
                                if (image.playerId == topTov[0].first.id) {
                                    imageResource = image.imageUrl
                                    break
                                }
                            }
                            image.load(imageResource)
                        }
                        secondPlayerCell.apply {
                            index.text = "2"
                            name.text = topTov[1].first.fullName()
                            club.text = topTov[1].first.team.abbreviation
                            statValue.text = topTov[1].second.toString()
                            var imageResource: Any = R.drawable.ic_player_2_small
                            for (image in favouriteImages) {
                                if (image.playerId == topTov[1].first.id) {
                                    imageResource = image.imageUrl
                                    break
                                }
                            }
                            image.load(imageResource)
                        }
                        thirdPlayerCell.apply {
                            index.text = "3"
                            name.text = topTov[2].first.fullName()
                            club.text = topTov[2].first.team.abbreviation
                            statValue.text = topTov[2].second.toString()
                            var imageResource: Any = R.drawable.ic_player_3_small
                            for (image in favouriteImages) {
                                if (image.playerId == topTov[2].first.id) {
                                    imageResource = image.imageUrl
                                    break
                                }
                            }
                            image.load(imageResource)
                        }
                    }
                }
            }
        }
    }
}