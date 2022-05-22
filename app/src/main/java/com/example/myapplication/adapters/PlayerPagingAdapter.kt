package com.example.myapplication.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.activities.PlayerDetailsActivity
import com.example.myapplication.databinding.PlayerItemViewBinding
import com.example.myapplication.models.Player

private const val EXTRA_PLAYER = "player"

class PlayerPagingAdapter(
    private val context: Context,
    diffCallback: DiffUtil.ItemCallback<Player>
) : PagingDataAdapter<Player, PlayerPagingAdapter.PlayerViewHolder>(diffCallback) {

    class PlayerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = PlayerItemViewBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.player_item_view, parent, false)
        return PlayerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val player = getItem(position)

        // TODO dodaj dohvacanje slika, zasad su samo placeholderi
        val imageResource = when (position) {
            1 -> R.drawable.ic_player_1_small
            2 -> R.drawable.ic_player_2_small
            else -> R.drawable.ic_player_3_small
        }
        holder.binding.icon.setBackgroundResource(imageResource)
        holder.binding.name.text = player?.fullName()
        holder.binding.team.text = player?.team?.abbreviation

        // TODO dodaj u favorite
        holder.binding.iconFavourite.setOnClickListener {
            holder.binding.iconFavourite.apply { isSelected = !isSelected }
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, PlayerDetailsActivity::class.java)
                .putExtra(EXTRA_PLAYER, player)
            context.startActivity(intent)
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