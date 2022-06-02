package com.example.myapplication.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.myapplication.adapters.PlayerDetailsPagerAdapter
import com.example.myapplication.database.NBAAppDatabase
import com.example.myapplication.databinding.ActivityPlayerDetailsBinding
import com.example.myapplication.models.Player
import com.example.myapplication.viewmodels.SharedViewModel
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.runBlocking

private const val EXTRA_PLAYER = "player"
private const val EXTRA_IS_FAVOURITE: String = "isFavourite"

class PlayerDetailsActivity : AppCompatActivity() {

    private val sharedViewModel: SharedViewModel by viewModels()

    private lateinit var binding: ActivityPlayerDetailsBinding
    lateinit var player: Player
    private var isFavourite: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlayerDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        player = intent.extras?.getSerializable(EXTRA_PLAYER) as Player
        isFavourite = intent.extras?.getSerializable(EXTRA_IS_FAVOURITE) as Boolean

        binding.iconFavourite.isSelected = isFavourite
        binding.iconFavourite.setOnClickListener {
            if (binding.iconFavourite.isSelected) {
                sharedViewModel.removeFavouritePlayer(this, player.id)
            } else {
                val lastPosition: Int
                val context = this
                runBlocking {
                    lastPosition = NBAAppDatabase.getDatabase(context)?.playersDao()
                        ?.getLastFavouritePlayerPosition() ?: 0
                }
                sharedViewModel.addFavouritePlayer(this, player.toFavouritePlayer(lastPosition + 1))
            }
            binding.iconFavourite.apply { isSelected = !isSelected }
        }

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