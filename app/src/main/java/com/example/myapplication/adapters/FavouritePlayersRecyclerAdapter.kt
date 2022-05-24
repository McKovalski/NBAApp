package com.example.myapplication.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.activities.PlayerDetailsActivity
import com.example.myapplication.databinding.FavouritePlayerViewBinding
import com.example.myapplication.fragments.FavouritesFragment
import com.example.myapplication.models.Player
import java.util.*

private const val EXTRA_PLAYER = "player"

@SuppressLint("NotifyDataSetChanged")
class FavouritePlayersRecyclerAdapter(
    private val context: Context,
    private val favouritePlayers: MutableList<Player>,
    private val fragment: FavouritesFragment
) : RecyclerView.Adapter<FavouritePlayersRecyclerAdapter.PlayerViewHolder>() {

    private var showReorder: Boolean = false

    fun switchReorder() {
        showReorder = !showReorder
        notifyDataSetChanged()
    }

    fun updateList(newFavourites: MutableList<Player>) {
        favouritePlayers.clear()
        favouritePlayers.addAll(newFavourites)
        notifyDataSetChanged()
    }

    class PlayerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = FavouritePlayerViewBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val view = LayoutInflater
            .from(context)
            .inflate(R.layout.favourite_player_view, parent, false)
        return PlayerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val player = favouritePlayers[position]

        // TODO dodaj dohvacanje slika, zasad su samo placeholderi
        val imageResource = when (position) {
            1 -> R.drawable.ic_player_1_small
            2 -> R.drawable.ic_player_2_small
            else -> R.drawable.ic_player_3_small
        }
        holder.binding.card.icon.setBackgroundResource(imageResource)
        holder.binding.card.name.text = player.fullName()
        holder.binding.card.team.text = player.team.abbreviation

        holder.binding.card.iconFavourite.isSelected = true
        holder.binding.card.iconFavourite.setOnClickListener {
            holder.binding.card.iconFavourite.isSelected = false
            fragment.removeFavouritePlayer(player.id)
            notifyItemRemoved(position)
            fragment.showRemovedFavouriteSnackbar(player.fullName())
        }

        if (showReorder) {
            holder.binding.reorderIcon.visibility = View.VISIBLE
        } else {
            holder.binding.reorderIcon.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, PlayerDetailsActivity::class.java)
                .putExtra(EXTRA_PLAYER, player)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return favouritePlayers.size
    }

    fun swapItems(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(favouritePlayers, i, i + 1)
            }
        } else {
            for (i in toPosition until fromPosition) {
                Collections.swap(favouritePlayers, i, i + 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }
}