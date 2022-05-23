package com.example.myapplication.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.example.myapplication.R
import com.example.myapplication.adapters.TeamDetailsPagerAdapter
import com.example.myapplication.databinding.ActivityTeamDetailsBinding
import com.example.myapplication.helpers.TeamsHelper
import com.example.myapplication.models.Team
import com.google.android.material.tabs.TabLayout

private const val EXTRA_TEAM = "team"
private const val EXTRA_TEAMS_IN_DIVISION = "teamsInDivision"

class TeamDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTeamDetailsBinding
    lateinit var team: Team
    lateinit var teamsInDivision: List<Team>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTeamDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        team = intent.extras?.getSerializable(EXTRA_TEAM) as Team
        teamsInDivision = intent.extras?.getSerializable(EXTRA_TEAMS_IN_DIVISION) as List<Team>

        setSupportActionBar(binding.toolbar)
        binding.arrowBack.setOnClickListener {
            finish()
        }
        binding.toolbarTitle.text = team.full_name
        val (logoId, colorId) = TeamsHelper().getLogoAndColor(team.name)
        binding.toolbar.setBackgroundResource(colorId)
        binding.tabLayout.setBackgroundResource(colorId)
        window.statusBarColor = ContextCompat.getColor(this, colorId)

        val teamDetailsPagerAdapter = TeamDetailsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = teamDetailsPagerAdapter
        val tabs: TabLayout = binding.tabLayout
        tabs.setupWithViewPager(viewPager)
    }
}