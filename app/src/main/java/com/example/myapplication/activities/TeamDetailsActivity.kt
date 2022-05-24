package com.example.myapplication.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.example.myapplication.R
import com.example.myapplication.adapters.TeamDetailsPagerAdapter
import com.example.myapplication.databinding.ActivityTeamDetailsBinding
import com.example.myapplication.helpers.TeamsHelper
import com.example.myapplication.models.Team
import com.example.myapplication.viewmodels.SharedViewModel
import com.google.android.material.tabs.TabLayout

private const val EXTRA_TEAM = "team"
private const val EXTRA_TEAMS_IN_DIVISION = "teamsInDivision"
private const val EXTRA_IS_FAVOURITE: String = "isFavourite"

class TeamDetailsActivity : AppCompatActivity() {

    private val sharedViewModel: SharedViewModel by viewModels()

    private lateinit var binding: ActivityTeamDetailsBinding
    lateinit var team: Team
    lateinit var teamsInDivision: List<Team>
    private var isFavourite: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTeamDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        team = intent.extras?.getSerializable(EXTRA_TEAM) as Team
        teamsInDivision = intent.extras?.getSerializable(EXTRA_TEAMS_IN_DIVISION) as List<Team>
        isFavourite = intent.extras?.getBoolean(EXTRA_IS_FAVOURITE) as Boolean

        binding.iconFavourite.isSelected = isFavourite
        binding.iconFavourite.setOnClickListener {
            if (binding.iconFavourite.isSelected) {
                sharedViewModel.removeFavouriteTeam(this, team.id)
            } else {
                sharedViewModel.getLastFavouriteTeamPosition(this)
                val newPosition = (sharedViewModel.lastFavouriteTeamPosition.value ?: 0).plus(1)
                sharedViewModel.addFavouriteTeam(this, team.toFavouriteTeam(newPosition))
            }
            binding.iconFavourite.apply { isSelected = !isSelected }
        }

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