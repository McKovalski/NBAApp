package com.example.myapplication.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.viewpager.widget.ViewPager
import com.example.myapplication.R
import com.example.myapplication.adapters.PlayerDetailsPagerAdapter
import com.example.myapplication.adapters.TeamDetailsPagerAdapter
import com.example.myapplication.databinding.ActivityPlayerDetailsBinding
import com.example.myapplication.models.Player
import com.google.android.material.tabs.TabLayout

private const val EXTRA_PLAYER = "player"

class PlayerDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerDetailsBinding
    lateinit var player: Player

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlayerDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        player = intent?.extras?.getSerializable(EXTRA_PLAYER) as Player

        setSupportActionBar(binding.toolbar)
        binding.arrowBack.setOnClickListener {
            finish()
        }
        binding.toolbarTitle.text = player.fullName()

        val playerDetailsPagerAdapter = PlayerDetailsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = playerDetailsPagerAdapter
        val tabs: TabLayout = binding.tabLayout
        tabs.setupWithViewPager(viewPager)
    }
}