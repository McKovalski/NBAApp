package com.example.myapplication.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.myapplication.models.FavouriteTeam
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

    @ExperimentalPagingApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)

        setupSpinner()

        return binding.root
    }

    override fun onResume() {
        /*val playerPagingAdapter = PlayerPagingAdapter(requireContext(), PlayerDiffCallback)
        binding.recyclerView.adapter = playerPagingAdapter*/
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
        binding.recyclerView.adapter = teamsRecyclerAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        sharedViewModel.getFavouriteTeams(requireContext())
        sharedViewModel.favouriteTeams.observe(viewLifecycleOwner) {
            val favourites = mutableListOf<Team>()
            favourites.addAll(it)
            teamsRecyclerAdapter.updateFavourites(favourites)
        }
        /*lifecycleScope.launch {
            sharedViewModel.getPlayerPaginatedFlow(requireContext()).collectLatest {
                playerPagingAdapter.submitData(it)
            }
        }*/
        super.onResume()
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_item, //TODO popraviti izgled dropdown menija
            arrayOf("Players", "Teams")
        )
        binding.spinner.adapter = adapter
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
}