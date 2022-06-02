package com.example.myapplication.activities

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.myapplication.adapters.MatchDetailsPagerAdapter
import com.example.myapplication.databinding.ActivityMatchDetailsBinding
import com.example.myapplication.models.Match
import com.google.android.material.tabs.TabLayout

private const val EXTRA_MATCH = "match"

class MatchDetailsActivity : AppCompatActivity() {

    lateinit var match: Match

    private lateinit var binding: ActivityMatchDetailsBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMatchDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        match = intent.extras?.getSerializable(EXTRA_MATCH) as Match

        setSupportActionBar(binding.toolbar)
        binding.arrowBack.setOnClickListener {
            finish()
        }

        binding.matchTeams.text = "${match.home_team.full_name} vs. ${match.visitor_team.full_name}"

        val matchDetailsPagerAdapter = MatchDetailsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = matchDetailsPagerAdapter
        val tabs: TabLayout = binding.tabLayout
        tabs.setupWithViewPager(viewPager)
    }
}