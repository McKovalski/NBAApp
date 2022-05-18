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
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Naziv igraca"

        val teamDetailsPagerAdapter = PlayerDetailsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = teamDetailsPagerAdapter
        val tabs: TabLayout = binding.tabLayout
        tabs.setupWithViewPager(viewPager)
    }

    // overridamo ovu metodu kako bi se vratili na prosli activity u stanju kojem smo ga ostavili
    // tj. ne pokrecemo ponovno home activity, vec samo gasimo ovaj
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}