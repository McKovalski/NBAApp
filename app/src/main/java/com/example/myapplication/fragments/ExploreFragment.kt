package com.example.myapplication.fragments

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.SearchView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapters.PlayerDiffCallback
import com.example.myapplication.adapters.PlayerPagingAdapter
import com.example.myapplication.adapters.PlayersFilteredRecyclerAdapter
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
    private val playersFilteredAdapter by lazy {
        PlayersFilteredRecyclerAdapter(
            requireContext(),
            mutableListOf(),
            mutableListOf(),
            this
        )
    }
    private val allTeams = mutableListOf<Team>()
    private val allImages = mutableListOf<PlayerImage>()
    private var isPlayersVisible: Boolean = true

    @ExperimentalPagingApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)

        setupSpinner()
        setPlayersVisibility(isPlayersVisible)

        binding.teamsRecyclerView.adapter = teamsRecyclerAdapter
        binding.teamsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.playersRecyclerView.adapter = playerPagingAdapter
        binding.playersRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.playersFilteredRecycler.adapter = playersFilteredAdapter
        binding.playersFilteredRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.playersFilteredRecycler.visibility = View.GONE
        lifecycleScope.launch {
            sharedViewModel.getPlayerPaginatedFlow(requireContext()).collectLatest {
                playerPagingAdapter.submitData(it)
            }
        }
        sharedViewModel.favouritePlayers.observe(viewLifecycleOwner) {
            val favourites = mutableListOf<Player>()
            favourites.addAll(it)
            playerPagingAdapter.updateFavourites(favourites)
            playersFilteredAdapter.updateFavourites(favourites)
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
                playersFilteredAdapter.updateImages(allImages)
            }
        }
        sharedViewModel.playersByName.observe(viewLifecycleOwner) {
            Log.d("filtered players", it.toString())
            playersFilteredAdapter.updatePlayers(it)
        }

        lifecycleScope.launch {
            playerPagingAdapter.loadStateFlow.collectLatest { loadState ->
                binding.progressBar.isVisible = loadState.refresh is LoadState.Loading
                binding.teamsRecyclerView.isVisible =
                    loadState.refresh !is LoadState.Loading && !isPlayersVisible
                binding.playersRecyclerView.isVisible =
                    loadState.refresh !is LoadState.Loading && isPlayersVisible
            }
        }

        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Hide keyboard
                val imm = requireContext()
                    .getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.searchBar.applicationWindowToken, 0)
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                // Threshold for teams is 1 letter
                // Threshold for players is 4 letters
                if (query != null && query.isNotEmpty()) {
                    if (isPlayersVisible && query.length > 3) {
                        showFilteredPlayers(true)
                        sharedViewModel.getPlayersByName(query)
                    } else {
                        val filteredTeams =
                            allTeams.filter { team ->
                                team.full_name.contains(
                                    query,
                                    true
                                )
                            } as MutableList<Team>
                        teamsRecyclerAdapter.updateTeams(filteredTeams)
                    }
                } else {
                    if (isPlayersVisible) {
                        showFilteredPlayers(false)
                    }
                    teamsRecyclerAdapter.updateTeams(allTeams)
                }
                return true
            }
        })

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
            R.layout.spinner_item,
            arrayOf(getString(R.string.players), getString(R.string.teams))
        )
        binding.spinner.adapter = adapter
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                sharedViewModel.spinnerSelectedPosition.value = position
                isPlayersVisible = position == 0
                setPlayersVisibility(isPlayersVisible)
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

    private fun showFilteredPlayers(show: Boolean) {
        if (show) {
            binding.playersFilteredRecycler.visibility = View.VISIBLE
            binding.playersRecyclerView.visibility = View.GONE
        } else {
            binding.playersFilteredRecycler.visibility = View.GONE
            binding.playersRecyclerView.visibility = View.VISIBLE
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
