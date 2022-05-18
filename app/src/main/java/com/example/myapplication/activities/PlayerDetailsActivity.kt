package com.example.myapplication.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.viewpager.widget.ViewPager
import com.example.myapplication.R
import com.example.myapplication.adapters.PlayerDetailsPagerAdapter
import com.example.myapplication.adapters.TeamDetailsPagerAdapter
import com.example.myapplication.databinding.ActivityPlayerDetailsBinding
import com.google.android.material.tabs.TabLayout

class PlayerDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlayerDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.arrowBack.setOnClickListener {
            finish()
        }
        //binding.toolbarTitle.text = "Neki title"

        val playerDetailsPagerAdapter = PlayerDetailsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = playerDetailsPagerAdapter
        val tabs: TabLayout = binding.tabLayout
        tabs.setupWithViewPager(viewPager)
    }
}