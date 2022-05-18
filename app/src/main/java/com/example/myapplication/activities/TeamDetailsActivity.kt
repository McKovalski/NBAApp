package com.example.myapplication.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.viewpager.widget.ViewPager
import com.example.myapplication.R
import com.example.myapplication.adapters.TeamDetailsPagerAdapter
import com.example.myapplication.databinding.ActivityTeamDetailsBinding
import com.google.android.material.tabs.TabLayout

class TeamDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTeamDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTeamDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.arrowBack.setOnClickListener {
            finish()
        }

        val teamDetailsPagerAdapter = TeamDetailsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = teamDetailsPagerAdapter
        val tabs: TabLayout = binding.tabLayout
        tabs.setupWithViewPager(viewPager)
    }
}