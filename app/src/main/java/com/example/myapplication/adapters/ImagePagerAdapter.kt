package com.example.myapplication.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import coil.load
import com.example.myapplication.R
import com.example.myapplication.databinding.PlayerImageItemBinding
import com.example.myapplication.models.PlayerImage

class ImagePagerAdapter(
    private val context: Context,
    private val images: MutableList<PlayerImage>
) : PagerAdapter() {

    fun updateImages(newImages: MutableList<PlayerImage>) {
        images.clear()
        images.addAll(newImages)
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return if (images.isNotEmpty()) images.size else 1
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view =
            LayoutInflater.from(context).inflate(R.layout.player_image_item, null, false)
        val binding = PlayerImageItemBinding.bind(view)

        Log.d("images u pageru", images.toString())

        if (images.isNotEmpty()) {
            binding.image.load(images[position].imageUrl)
        } else {
            binding.image.load(R.drawable.ic_3_big)
        }

        val viewPager = container as ViewPager
        viewPager.addView(view, 0)

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}