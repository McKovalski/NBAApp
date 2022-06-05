package com.example.myapplication.fragments

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.FrameLayout
import androidx.appcompat.widget.SearchView
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapters.*
import com.example.myapplication.databinding.FilterMatchesBottomSheetLayoutBinding
import com.example.myapplication.databinding.FragmentExploreBinding
import com.example.myapplication.helpers.TeamsHelper
import com.example.myapplication.models.*
import com.example.myapplication.viewmodels.SharedViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val FRAGMENT_TYPE = "ExploreTeams"

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
    private val filterAdapter by lazy {
        FilterRecyclerAdapter(
            requireContext(),
            mutableListOf(),
            mutableListOf(),
            this,
            FRAGMENT_TYPE
        )
    }
    private val allTeams = mutableListOf<Team>()
    private val teams = mutableListOf<Team>()
    private val allImages = mutableListOf<PlayerImage>()
    private var isPlayersVisible: Boolean = true
    private var selectedConference: String? = null
    private var selectedDivision: String? = null
    private val filters = mutableListOf<String>()
    private val filterTypes = mutableListOf<FilterType>()

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
        binding.filterActionBar.recycler.adapter = filterAdapter
        binding.filterActionBar.recycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

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
            teams.clear()
            teams.addAll(allTeams)
            teamsRecyclerAdapter.updateTeams(teams)
            teamsRecyclerAdapter.setAllTeams(teams)
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
                            teams.filter { team ->
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
                    teamsRecyclerAdapter.updateTeams(teams)
                }
                return true
            }
        })

        setTeamFilterBottomSheet()

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

    // Reusing the match filter bottom sheet layout
    private fun setTeamFilterBottomSheet() {
        val dialog = BottomSheetDialog(requireContext())
        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.filter_matches_bottom_sheet_layout, null)
        val bottomSheetBinding = FilterMatchesBottomSheetLayoutBinding.bind(view)
        dialog.setContentView(view)

        bottomSheetBinding.teamInputLayout.setHint(getString(R.string.conference))
        bottomSheetBinding.seasonInputLayout.setHint(getString(R.string.division))

        // Set the button as non clickable until user selects an item
        bottomSheetBinding.buttonApply.apply {
            alpha = 0.5f
            isClickable = false
            isEnabled = false
        }
        bottomSheetBinding.buttonCancel.setOnClickListener {
            dialog.dismiss()
        }
        bottomSheetBinding.buttonApply.setOnClickListener {
            val conferenceFilter = bottomSheetBinding.teamAutocomplete.text.toString()
            val divisionFilter = bottomSheetBinding.seasonAutocomplete.text.toString()
            teams.clear()
            teams.addAll(allTeams)
            if (conferenceFilter.isNotEmpty()) {
                val conference = getString(TeamsHelper().getModelConferenceName(conferenceFilter))
                teams.retainAll { t -> t.conference == conference }
            } else {
                selectedConference = null
                val index = filterTypes.indexOf(FilterType.CONFERENCE)
                if (index != -1) {
                    filters.removeAt(index)
                    filterTypes.removeAt(index)
                }
            }
            if (divisionFilter.isNotEmpty()) {
                teams.retainAll { t -> t.division == divisionFilter }
            } else {
                selectedDivision = null
                val index = filterTypes.indexOf(FilterType.DIVISION)
                if (index != -1) {
                    filters.removeAt(index)
                    filterTypes.removeAt(index)
                }
            }
            teamsRecyclerAdapter.updateTeams(teams)
            filterAdapter.updateFilters(filters)
            filterAdapter.updateFilterTypes(filterTypes)
            binding.filterActionBar.root.visibility = View.VISIBLE
            // Clear the autocomplete text views and...
            bottomSheetBinding.root.children.forEach {
                when (it) {
                    is TextInputLayout -> {
                        // TextInputLayout has a single child which is a FrameLayout
                        // our AutoCompleteTextView is a child of that FrameLayout
                        val frameLayout: FrameLayout = it.children.first() as FrameLayout
                        val autoCompleteTextView = frameLayout.children.first()
                        if (autoCompleteTextView is AutoCompleteTextView) {
                            autoCompleteTextView.text?.clear()
                            autoCompleteTextView.clearFocus()
                        }
                    }
                }
            }
            // ...disable the apply button
            bottomSheetBinding.buttonApply.apply {
                alpha = 0.5f
                isClickable = false
                isEnabled = false
            }
            dialog.dismiss()
        }
        val conferenceAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            listOf(getString(R.string.western), getString(R.string.eastern))
        )
        bottomSheetBinding.teamAutocomplete.setAdapter(conferenceAdapter)
        bottomSheetBinding.teamAutocomplete.setOnItemClickListener { adapterView, _, position, _ ->
            selectedConference = adapterView?.getItemAtPosition(position) as String
            // Enable the apply button
            bottomSheetBinding.buttonApply.apply {
                alpha = 1f
                isClickable = true
                isEnabled = true
            }
            val index = filterTypes.indexOf(FilterType.CONFERENCE)
            if (index != -1) {
                filters.removeAt(index)
                filters.add(index, selectedConference!!)
            } else {
                filterTypes.add(FilterType.CONFERENCE)
                filters.add(selectedConference!!)
            }
        }

        val divisionAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            listOf(
                getString(R.string.atlantic),
                getString(R.string.central),
                getString(R.string.northwest),
                getString(R.string.pacific),
                getString(R.string.southeast),
                getString(R.string.southwest)
            )
        )
        bottomSheetBinding.seasonAutocomplete.setAdapter(divisionAdapter)
        bottomSheetBinding.seasonAutocomplete.setOnItemClickListener { adapterView, _, position, _ ->
            selectedDivision = adapterView?.getItemAtPosition(position) as String
            // Enable the apply button
            bottomSheetBinding.buttonApply.apply {
                alpha = 1f
                isClickable = true
                isEnabled = true
            }
            val index = filterTypes.indexOf(FilterType.DIVISION)
            if (index != -1) {
                filters.removeAt(index)
                filters.add(index, selectedDivision!!)
            } else {
                filterTypes.add(FilterType.DIVISION)
                filters.add(selectedDivision!!)
            }
        }

        binding.iconFilter.setOnClickListener {
            if (!isPlayersVisible) {
                dialog.show()
            }
        }
    }

    fun removeTeamFilter(filterType: FilterType) {
        val index = filterTypes.indexOf(filterType)
        if (index != -1) {
            filters.removeAt(index)
            filterTypes.removeAt(index)
        }
        if (filters.isEmpty()) {
            binding.filterActionBar.root.visibility = View.GONE
        }
        when (filterType) {
            FilterType.CONFERENCE -> selectedConference = null
            FilterType.DIVISION -> selectedDivision = null
            else -> Unit
        }
        teams.clear()
        teams.addAll(allTeams)
        if (selectedConference != null) {
            val conference = getString(TeamsHelper().getModelConferenceName(selectedConference!!))
            teams.retainAll { t -> t.conference == conference }
        } else if (selectedDivision != null) {
            teams.retainAll { t -> t.division == selectedDivision }
        }
        teamsRecyclerAdapter.updateTeams(teams)
        filterAdapter.updateFilters(filters)
        filterAdapter.updateFilterTypes(filterTypes)
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
