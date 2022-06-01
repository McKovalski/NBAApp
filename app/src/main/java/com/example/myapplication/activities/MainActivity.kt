package com.example.myapplication.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.fragments.ExploreFragment
import com.example.myapplication.fragments.FavouritesFragment
import com.example.myapplication.fragments.SeasonsFragment
import com.example.myapplication.fragments.SettingsFragment
import com.example.myapplication.viewmodels.SharedViewModel

class MainActivity : AppCompatActivity() {

    private val sharedViewModel: SharedViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // TODO stavi na login fragment ako je prvi put upaljen app (ili nesto slicno)...
        setCurrentFragment(ExploreFragment())

        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.explore -> setCurrentFragment(ExploreFragment())
                R.id.favourites -> setCurrentFragment(FavouritesFragment())
                R.id.seasons -> setCurrentFragment(SeasonsFragment())
                R.id.settings -> setCurrentFragment(SettingsFragment())
            }
            true
        }
    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            commit()
        }
}