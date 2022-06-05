package com.example.myapplication.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.myapplication.R
import com.example.myapplication.fragments.PlayerDetailsFragment
import com.example.myapplication.models.PlayerImage

@SuppressLint("NotifyDataSetChanged")
class PhotosRecyclerAdapter(
    private val context: Context,
    private val photos: MutableList<PlayerImage>,
    private val fragment: Fragment
) : RecyclerView.Adapter<HighlightsRecyclerAdapter.HighlightViewHolder>() {

    private var showEdit = false
    private lateinit var favouritePhoto: PlayerImage

    fun setFavouritePhoto(photo: PlayerImage) {
        favouritePhoto = photo
        notifyDataSetChanged()
    }

    fun switchEdit() {
        showEdit = !showEdit
        notifyDataSetChanged()
    }

    fun updatePhotos(p: List<PlayerImage>) {
        photos.clear()
        photos.addAll(p)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HighlightsRecyclerAdapter.HighlightViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.highlight_card, parent, false)
        return HighlightsRecyclerAdapter.HighlightViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: HighlightsRecyclerAdapter.HighlightViewHolder,
        position: Int
    ) {
        val photo = photos[position]

        holder.binding.caption.text = photo.imageCaption
        holder.binding.image.load(photo.imageUrl)

        if (!showEdit) {
            holder.binding.root.setCardBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.surface_surface_0
                )
            )
            if (photo == favouritePhoto) {
                holder.binding.iconDelete.setImageResource(R.drawable.ic_thumbnail_selected)
                holder.binding.iconDelete.imageTintList =
                    ContextCompat.getColorStateList(context, R.color.color_secondary)
            } else {
                holder.binding.iconDelete.setImageResource(R.drawable.ic_thumbnail_unselected)
                holder.binding.iconDelete.imageTintList =
                    ContextCompat.getColorStateList(context, R.color.neutrals_n_lv_2)
            }
            holder.binding.iconDelete.setOnClickListener {
                if (photo != favouritePhoto) {
                    (fragment as PlayerDetailsFragment).setFavouritePhoto(photo)
                }
            }
        } else {
            holder.binding.root.setCardBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.surface_surface_2
                )
            )
            holder.binding.iconDelete.setImageResource(R.drawable.ic_delete)
            holder.binding.iconDelete.imageTintList =
                ContextCompat.getColorStateList(context, R.color.status_error)
            holder.binding.iconDelete.setOnClickListener {
                (fragment as PlayerDetailsFragment).deletePlayerPhoto(photo)
            }
        }
    }

    override fun getItemCount(): Int {
        return photos.size
    }
}