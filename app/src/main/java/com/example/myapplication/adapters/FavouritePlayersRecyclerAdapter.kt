package com.example.myapplication.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.myapplication.R
import com.example.myapplication.activities.PlayerDetailsActivity
import com.example.myapplication.databinding.FavouritePlayerViewBinding
import com.example.myapplication.fragments.FavouritesFragment
import com.example.myapplication.models.Player
import com.example.myapplication.models.PlayerImage
import java.util.*

private const val EXTRA_PLAYER = "player"
private const val EXTRA_IS_FAVOURITE: String = "isFavourite"
private const val TYPE_EMPTY_STATE = 0
private const val TYPE_PLAYER = 1

@SuppressLint("NotifyDataSetChanged")
class FavouritePlayersRecyclerAdapter(
    private val context: Context,
    private val favouritePlayers: MutableList<Player>,
    private val fragment: FavouritesFragment
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var showReorder: Boolean = false
    private val favouriteImages = mutableListOf<PlayerImage>()

    fun switchReorder() {
        showReorder = !showReorder
        notifyDataSetChanged()
    }

    fun updateList(newFavourites: MutableList<Player>) {
        favouritePlayers.clear()
        favouritePlayers.addAll(newFavourites)
        notifyDataSetChanged()
    }

    fun updateFavouriteImages(newImages: MutableList<PlayerImage>) {
        favouriteImages.clear()
        favouriteImages.addAll(newImages)
        notifyDataSetChanged()
    }

    class PlayerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = FavouritePlayerViewBinding.bind(view)
    }

    override fun getItemViewType(position: Int): Int {
        return if (favouritePlayers.size == 0) TYPE_EMPTY_STATE else TYPE_PLAYER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_EMPTY_STATE -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.search_empty_state, parent, false)
                TeamsRecyclerAdapter.TeamEmptyStateViewHolder(view)
            }
            TYPE_PLAYER -> {
                val view =
                    LayoutInflater.from(context)
                        .inflate(R.layout.favourite_player_view, parent, false)
                PlayerViewHolder(view)
            }
            else -> throw IllegalAccessException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TeamsRecyclerAdapter.TeamEmptyStateViewHolder) {
            holder.binding.image.load(R.drawable.ic_sad_star)
            holder.binding.image.imageTintList =
                ContextCompat.getColorStateList(context, R.color.neutrals_n_lv_2)
            holder.binding.topText.text = context.getString(R.string.no_favourite_players)
            holder.binding.bottomText.text =
                context.getString(R.string.add_favourite_players_in_explore)
        } else if (holder is PlayerViewHolder) {
            val player = favouritePlayers[position]

            if (favouriteImages.isNotEmpty() && player.id in favouriteImages.map { i -> i.playerId }) {
                for (image in favouriteImages) {
                    if (image.playerId == player.id) {
                        holder.binding.card.icon.load(image.imageUrl)
                        break
                    }
                }
            } else {
                val imageResource = when (position) {
                    1 -> R.drawable.ic_player_1_small
                    2 -> R.drawable.ic_player_2_small
                    else -> R.drawable.ic_player_3_small
                }
                holder.binding.card.icon.load(imageResource)
            }
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
                    .putExtra(EXTRA_IS_FAVOURITE, true)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return if (favouritePlayers.size > 0) favouritePlayers.size else 1
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