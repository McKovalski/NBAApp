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
import com.example.myapplication.models.*
import com.example.myapplication.viewmodels.SharedViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ExploreFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var _binding: FragmentExploreBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val playerPagingAdapter by lazy {
        PlayerPagingAdapter(
            requireContext(),
            mutableListOf(),
            this,
            PlayerDiffCallback
        )
    }
    private val teamsRecyclerAdapter by lazy {
        TeamsRecyclerAdapter(
            requireContext(),
            mutableListOf(),
            mutableListOf(),
            this
        )
    }
    private val allTeams = mutableListOf<Team>()
    private val allImages = mutableListOf<PlayerImage>()

    @ExperimentalPagingApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)

        setupSpinner()
        setPlayersVisibility(true)

        binding.teamsRecyclerView.adapter = teamsRecyclerAdapter
        binding.teamsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.playersRecyclerView.adapter = playerPagingAdapter
        binding.playersRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        lifecycleScope.launch {
            sharedViewModel.getPlayerPaginatedFlow(requireContext()).collectLatest {
                playerPagingAdapter.submitData(it)
            }
        }
        sharedViewModel.favouritePlayers.observe(viewLifecycleOwner) {
            val favourites = mutableListOf<Player>()
            favourites.addAll(it)
            playerPagingAdapter.updateFavourites(favourites)
        }
        sharedViewModel.allTeams.observe(viewLifecycleOwner) {
            allTeams.clear()
            allTeams.addAll(it)
            teamsRecyclerAdapter.updateTeams(allTeams)
        }
        sharedViewModel.favouriteTeams.observe(viewLifecycleOwner) {
            val favourites = mutableListOf<Team>()
            favourites.addAll(it)
            teamsRecyclerAdapter.updateFavourites(favourites)
        }
        sharedViewModel.playerImages.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                allImages.addAll(it)
                playerPagingAdapter.updateImages(allImages)
            }
        }

        return binding.root
    }

    @ExperimentalPagingApi
    override fun onResume() {
        binding.spinner.setSelection(sharedViewModel.spinnerSelectedPosition.value ?: 0)

        sharedViewModel.getFavouritePlayers(requireContext())
        sharedViewModel.getAllTeams(requireContext())
        sharedViewModel.getFavouriteTeams(requireContext())
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

    fun addFavouritePlayer(favouritePlayer: FavouritePlayer) {
        sharedViewModel.addFavouritePlayer(requireContext(), favouritePlayer)
    }

    fun removeFavouritePlayer(id: Int) {
        sharedViewModel.removeFavouritePlayer(requireContext(), id)
    }

    fun getPlayerImages(playerId: Int) {
        sharedViewModel.getPlayerImages(playerId)
    }

    fun setPlayerFavouriteImage(image: PlayerImage) {
        sharedViewModel.setPlayerFavouriteImage(requireContext(), image)
    }
}
