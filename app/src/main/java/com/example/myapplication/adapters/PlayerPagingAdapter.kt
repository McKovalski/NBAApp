package com.example.myapplication.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.myapplication.R
import com.example.myapplication.activities.PlayerDetailsActivity
import com.example.myapplication.database.NBAAppDatabase
import com.example.myapplication.databinding.PlayerItemViewBinding
import com.example.myapplication.fragments.ExploreFragment
import com.example.myapplication.models.Player
import com.example.myapplication.models.PlayerImage
import kotlinx.coroutines.runBlocking

private const val EXTRA_PLAYER = "player"
private const val EXTRA_IS_FAVOURITE: String = "isFavourite"

@SuppressLint("NotifyDataSetChanged")
class PlayerPagingAdapter(
    private val context: Context,
    private val favouritePlayers: MutableList<Player>,
    private val fragment: ExploreFragment,
    diffCallback: DiffUtil.ItemCallback<Player>
) : PagingDataAdapter<Player, PlayerPagingAdapter.PlayerViewHolder>(diffCallback) {

    private val allImages = mutableListOf<PlayerImage>()

    fun updateFavourites(newFavourites: MutableList<Player>) {
        favouritePlayers.clear()
        favouritePlayers.addAll(newFavourites)
        notifyDataSetChanged()
    }

    fun updateImages(newImages: MutableList<PlayerImage>) {
        allImages.clear()
        allImages.addAll(newImages)
        notifyDataSetChanged()
    }

    class PlayerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = PlayerItemViewBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.player_item_view, parent, false)
        return PlayerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val player = getItem(position)
        var favouriteImage: PlayerImage? = null

        runBlocking {
            favouriteImage = NBAAppDatabase.getDatabase(context)?.playersDao()
                ?.getPlayerFavouriteImage(player?.id!!)
        }

        if (player != null) {
            if (favouriteImage != null) {
                holder.binding.icon.load(favouriteImage?.imageUrl)
            } else {
                if (allImages.isNotEmpty() && player.id in allImages.map { i -> i.playerId }) {
                    for (image in allImages) {
                        if (image.playerId == player.id) {
                            holder.binding.icon.load(image.imageUrl)
                            fragment.setPlayerFavouriteImage(image)
                            break
                        }
                    }
                } else {
                    val imageResource = when (position) {
                        1 -> R.drawable.ic_player_1_small
                        2 -> R.drawable.ic_player_2_small
                        else -> R.drawable.ic_player_3_small
                    }
                    holder.binding.icon.load(imageResource)
                    fragment.getPlayerImages(player.id)
                }
            }
            holder.binding.name.text = player.fullName()
            holder.binding.team.text = player.team.abbreviation

            holder.binding.iconFavourite.isSelected = player in favouritePlayers
            holder.binding.iconFavourite.setOnClickListener {
                if (holder.binding.iconFavourite.isSelected) {
                    favouritePlayers.remove(player)
                    fragment.removeFavouritePlayer(player.id)
                } else {
                    favouritePlayers.add(player)
                    val lastPosition: Int
                    runBlocking {
                        lastPosition = NBAAppDatabase.getDatabase(context)?.playersDao()
                            ?.getLastFavouritePlayerPosition() ?: 0
                    }
                    fragment.addFavouritePlayer(player.toFavouritePlayer(lastPosition + 1))
                }
                holder.binding.iconFavourite.apply { isSelected = !isSelected }
            }

            holder.itemView.setOnClickListener {
                val intent = Intent(context, PlayerDetailsActivity::class.java)
                    .putExtra(EXTRA_PLAYER, player)
                    .putExtra(EXTRA_IS_FAVOURITE, holder.binding.iconFavourite.isSelected)
                context.startActivity(intent)
            }
        }
    }
}

object PlayerDiffCallback : DiffUtil.ItemCallback<Player>() {
    override fun areItemsTheSame(oldItem: Player, newItem: Player): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Player, newItem: Player): Boolean {
        return oldItem == newItem
    }

}