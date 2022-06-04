package com.example.myapplication.adapters

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.myapplication.R
import com.example.myapplication.databinding.HighlightCardBinding
import com.example.myapplication.databinding.HighlightEmptyStateBinding
import com.example.myapplication.fragments.MatchDetailsFragment
import com.example.myapplication.fragments.TeamDetailsFragment
import com.example.myapplication.helpers.YoutubeVideoHelper
import com.example.myapplication.models.Highlight
import com.example.myapplication.models.PlayerImage

private const val TYPE_EMPTY_STATE = 0
private const val TYPE_HIGHLIGHT = 1

@SuppressLint("NotifyDataSetChanged")
class HighlightsRecyclerAdapter(
    private val context: Context,
    private val highlights: MutableList<Highlight>,
    private val fragment: Fragment
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var showReorder: Boolean = false

    fun switchReorder() {
        showReorder = !showReorder
        notifyDataSetChanged()
    }

    fun updateHighlights(h: List<Highlight>) {
        highlights.clear()
        highlights.addAll(h)
        notifyDataSetChanged()
    }

    class HighlightViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = HighlightCardBinding.bind(view)
    }

    class HighlightEmptyStateViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = HighlightEmptyStateBinding.bind(view)
    }

    override fun getItemViewType(position: Int): Int {
        return if (highlights.size == 0) TYPE_EMPTY_STATE else TYPE_HIGHLIGHT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_EMPTY_STATE -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.highlight_empty_state, parent, false)
                HighlightEmptyStateViewHolder(view)
            }
            TYPE_HIGHLIGHT -> {
                val view =
                    LayoutInflater.from(context).inflate(R.layout.highlight_card, parent, false)
                HighlightViewHolder(view)
            }
            else -> throw IllegalAccessException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HighlightEmptyStateViewHolder) {
            holder.binding.buttonAdd.setOnClickListener {
                (fragment as MatchDetailsFragment).setBottomSheetDialog()
            }
        } else if (holder is HighlightViewHolder) {
            val highlight = highlights[position]

            val videoId = YoutubeVideoHelper().getVideoId(highlight.url)
            val videoThumbnailUrl = YoutubeVideoHelper().getVideoThumbnailUrl(videoId!!)
            Log.d("videoThumbnailUrl", videoThumbnailUrl)
            holder.binding.image.load(videoThumbnailUrl)
            holder.binding.caption.text = highlight.name

            holder.binding.iconDelete.isVisible = showReorder
            holder.binding.card.isClickable = !showReorder
            holder.binding.card.isFocusable = !showReorder

            holder.binding.iconDelete.setOnClickListener {
                (fragment as MatchDetailsFragment).deleteHighlight(highlight.id)
            }

            holder.binding.card.setOnClickListener {
                val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$videoId"))
                val webIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=$videoId")
                )
                try {
                    context.startActivity(appIntent)
                } catch (e: ActivityNotFoundException) {
                    context.startActivity(webIntent)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return if (highlights.size > 0) highlights.size else 1
    }
}