package com.example.myapplication.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myapplication.models.FavouritePlayer
import com.example.myapplication.models.Player
import com.example.myapplication.models.PlayerRemoteKey
import com.example.myapplication.models.Team

@Database(
    entities = [Player::class, FavouritePlayer::class, Team::class, PlayerRemoteKey::class],
    version = 1,
    exportSchema = false
)
abstract class NBAAppDatabase : RoomDatabase() {
    abstract fun playersDao(): PlayersDao
    abstract fun playerRemoteKeyDao(): PlayerRemoteKeyDao

    companion object {
        private var instance: NBAAppDatabase? = null

        fun getDatabase(context: Context): NBAAppDatabase? {
            if (instance == null) {
                instance = buildDatabase(context)
            }
            return instance
        }

        private fun buildDatabase(context: Context): NBAAppDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                NBAAppDatabase::class.java,
                "NBAAppDatabase"
            ).build()
    }
}