package com.example.myapplication.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapters.PlayerDiffCallback
import com.example.myapplication.adapters.PlayerPagingAdapter
import com.example.myapplication.adapters.TeamsRecyclerAdapter
import com.example.myapplication.databinding.FragmentExploreBinding
import com.example.myapplication.models.FavouritePlayer
import com.example.myapplication.models.FavouriteTeam
import com.example.myapplication.models.Player
import com.example.myapplication.models.Team
import com.example.myapplication.viewmodels.SharedViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ExploreFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var _binding: FragmentExploreBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)

        setupSpinner()
        setPlayersVisibility(true)

        return binding.root
    }

    @ExperimentalPagingApi
    override fun onResume() {
        binding.spinner.setSelection(sharedViewModel.spinnerSelectedPosition.value ?: 0)

        val playerPagingAdapter = PlayerPagingAdapter(
            requireContext(),
            mutableListOf(),
            this,
            PlayerDiffCallback
        )
        binding.playersRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.playersRecyclerView.adapter = playerPagingAdapter
        lifecycleScope.launch {
            sharedViewModel.getPlayerPaginatedFlow(requireContext()).collectLatest {
                playerPagingAdapter.submitData(it)
            }
        }
        sharedViewModel.getFavouritePlayers(requireContext())
        sharedViewModel.favouritePlayers.observe(viewLifecycleOwner) {
            val favourites = mutableListOf<Player>()
            favourites.addAll(it)
            playerPagingAdapter.updateFavourites(favourites)
        }

        val allTeams = mutableListOf<Team>()
        sharedViewModel.getAllTeams(requireContext())
        sharedViewModel.allTeams.observe(viewLifecycleOwner) {
            allTeams.addAll(it)
        }
        val teamsRecyclerAdapter = TeamsRecyclerAdapter(
            requireContext(),
            allTeams,
            mutableListOf(),
            this
        )
        binding.teamsRecyclerView.adapter = teamsRecyclerAdapter
        binding.teamsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        sharedViewModel.getFavouriteTeams(requireContext())
        sharedViewModel.favouriteTeams.observe(viewLifecycleOwner) {
            val favourites = mutableListOf<Team>()
            favourites.addAll(it)
            teamsRecyclerAdapter.updateFavourites(favourites)
        }
        super.onResume()
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_item, //TODO popraviti izgled dropdown menija
            arrayOf(getString(R.string.players), getString(R.string.teams))
        )
        binding.spinner.adapter = adapter
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                sharedViewModel.spinnerSelectedPosition.value = position
                if (position == 0) {
                    setPlayersVisibility(true)
                } else {
                    setPlayersVisibility(false)
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                // ne treba
            }

        }
    }

    private fun setPlayersVisibility(isVisible: Boolean) {
        if (isVisible) {
            binding.playersRecyclerView.visibility = View.VISIBLE
            binding.title.text = getString(R.string.all_players)
            binding.teamsRecyclerView.visibility = View.GONE
        } else {
            binding.playersRecyclerView.visibility = View.GONE
            binding.title.text = getString(R.string.all_teams)
            binding.teamsRecyclerView.visibility = View.VISIBLE
        }
    }

    fun addFavouriteTeam(favouriteTeam: FavouriteTeam) {
        sharedViewModel.addFavouriteTeam(requireContext(), favouriteTeam)
    }

    fun removeFavouriteTeam(teamId: Int) {
        sharedViewModel.removeFavouriteTeam(requireContext(), teamId)
    }

    fun getLastFavouriteTeamPosition(): Int {
        sharedViewModel.getLastFavouriteTeamPosition(requireContext())
        return sharedViewModel.lastFavouriteTeamPosition.value ?: 0
    }

    fun addFavouritePlayer(favouritePlayer: FavouritePlayer) {
        sharedViewModel.addFavouritePlayer(requireContext(), favouritePlayer)
    }

    fun removeFavouritePlayer(id: Int) {
        sharedViewModel.removeFavouritePlayer(requireContext(), id)
    }

    fun getLastFavouritePlayerPosition(): Int {
        sharedViewModel.getLastFavouritePlayerPosition(requireContext())
        return sharedViewModel.lastFavouritePlayerPosition.value ?: 0
    }
}
