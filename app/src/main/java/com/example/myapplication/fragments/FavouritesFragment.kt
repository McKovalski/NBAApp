package com.example.myapplication.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.activities.MainActivity
import com.example.myapplication.adapters.FavouritePlayersRecyclerAdapter
import com.example.myapplication.adapters.FavouriteTeamsRecyclerAdapter
import com.example.myapplication.databinding.FragmentFavouritesBinding
import com.example.myapplication.models.Player
import com.example.myapplication.models.Team
import com.example.myapplication.viewmodels.SharedViewModel
import com.google.android.material.snackbar.Snackbar
import java.util.*

class FavouritesFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var _binding: FragmentFavouritesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var isEditing = false
    var favouritePlayers = mutableListOf<Player>()
    var favouriteTeams = mutableListOf<Team>()
    private val favouritePlayersAdapter by lazy {
        FavouritePlayersRecyclerAdapter(
            requireContext(),
            mutableListOf(),
            this
        )
    }
    private val favouriteTeamsAdapter by lazy {
        FavouriteTeamsRecyclerAdapter(
            requireContext(),
            mutableListOf(),
            this
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouritesBinding.inflate(inflater, container, false)

        binding.recyclerPlayers.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerPlayers.adapter = favouritePlayersAdapter
        binding.recyclerTeams.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerTeams.adapter = favouriteTeamsAdapter

        val playerItemTouchHelper = ItemTouchHelper(playerItemTouchHelperCallback)
        val teamItemTouchHelper = ItemTouchHelper(teamItemTouchHelperCallback)
        playerItemTouchHelper.attachToRecyclerView(binding.recyclerPlayers)
        teamItemTouchHelper.attachToRecyclerView(binding.recyclerTeams)

        binding.iconEdit.setOnClickListener {
            if (favouritePlayers.isNotEmpty() || favouriteTeams.isNotEmpty()) {
                if (!isEditing) {
                    isEditing = true
                    binding.iconEdit.setImageResource(R.drawable.ic_done)
                } else {
                    isEditing = false
                    binding.iconEdit.setImageResource(R.drawable.ic_edit)
                    val favouritePlayersDb = favouritePlayers.mapIndexed { index, player ->
                        player.toFavouritePlayer(index + 1)
                    }
                    val favouriteTeamsDb = favouriteTeams.mapIndexed { index, team ->
                        team.toFavouriteTeam(index + 1)
                    }
                    sharedViewModel.addAllFavouritePlayers(requireContext(), favouritePlayersDb)
                    sharedViewModel.addAllFavouriteTeams(requireContext(), favouriteTeamsDb)
                }
                favouritePlayersAdapter.switchReorder()
                favouriteTeamsAdapter.switchReorder()
            }
        }

        return binding.root
    }

    override fun onResume() {
        sharedViewModel.getFavouritePlayers(requireContext())
        sharedViewModel.getFavouriteTeams(requireContext())
        sharedViewModel.favouritePlayers.observe(viewLifecycleOwner) {
            favouritePlayers.clear()
            favouritePlayers.addAll(it)
            favouritePlayersAdapter.updateList(favouritePlayers)
        }
        sharedViewModel.favouriteTeams.observe(viewLifecycleOwner) {
            favouriteTeams.clear()
            favouriteTeams.addAll(it)
            favouriteTeamsAdapter.updateList(favouriteTeams)
        }
        super.onResume()
    }

    fun removeFavouritePlayer(id: Int) {
        sharedViewModel.removeFavouritePlayer(requireContext(), id)
    }

    fun removeFavouriteTeam(teamId: Int) {
        sharedViewModel.removeFavouriteTeam(requireContext(), teamId)
    }

    fun getAllTeams(): List<Team>? {
        sharedViewModel.getAllTeams(requireContext())
        return sharedViewModel.allTeams.value
    }

    fun showRemovedFavouriteSnackbar(title: String) {
        val snackbar = Snackbar.make(
            requireView(),
            getString(R.string.removed_from_favourites, title),
            Snackbar.LENGTH_LONG
        )
        val textView =
            snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_close_white, 0)
        snackbar.setAnchorView(R.id.bottomNavigationView)
        snackbar.view.setPadding(16, 18, 16, 18)
        snackbar.view.setBackgroundResource(R.drawable.snackbar_background)
        textView.setOnClickListener {
            snackbar.dismiss()
        }
        snackbar.show()
    }

    private val playerItemTouchHelperCallback = object : ItemTouchHelper.Callback() {
        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            val flags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
            return makeMovementFlags(flags, 0)
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val fromPosition = viewHolder.adapterPosition
            val toPosition = target.adapterPosition
            if (fromPosition < toPosition) {
                for (i in fromPosition until toPosition) {
                    Collections.swap(favouritePlayers, i, i + 1)
                }
            } else {
                for (i in toPosition until fromPosition) {
                    Collections.swap(favouritePlayers, i, i + 1)
                }
            }
            (binding.recyclerPlayers.adapter as FavouritePlayersRecyclerAdapter).swapItems(
                viewHolder.adapterPosition,
                target.adapterPosition
            )
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            // ne treba nam
        }
    }

    private val teamItemTouchHelperCallback = object : ItemTouchHelper.Callback() {
        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            val flags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
            return makeMovementFlags(flags, 0)
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val fromPosition = viewHolder.adapterPosition
            val toPosition = target.adapterPosition
            if (fromPosition < toPosition) {
                for (i in fromPosition until toPosition) {
                    Collections.swap(favouriteTeams, i, i + 1)
                }
            } else {
                for (i in toPosition until fromPosition) {
                    Collections.swap(favouriteTeams, i, i + 1)
                }
            }
            (binding.recyclerTeams.adapter as FavouriteTeamsRecyclerAdapter).swapItems(
                viewHolder.adapterPosition,
                target.adapterPosition
            )
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            // ne treba nam
        }
    }
}