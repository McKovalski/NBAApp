package com.example.myapplication.database

import androidx.paging.PagingSource
import androidx.room.*
import com.example.myapplication.models.FavouritePlayer
import com.example.myapplication.models.Player
import com.example.myapplication.models.PlayerImage

@Dao
interface PlayersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(players: List<Player>)

    @Query("SELECT * FROM Player ORDER BY last_name")
    fun pagingSource(): PagingSource<Int, Player>

    @Query("DELETE FROM Player")
    suspend fun clearAll()

    @Query("SELECT * FROM FavouritePlayer ORDER BY dbOrderPosition")
    suspend fun getAllFavouritePlayers(): List<FavouritePlayer>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavouritePlayer(favouritePlayer: FavouritePlayer)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFavouritePlayers(favouritePlayers: List<FavouritePlayer>)

    @Query("DELETE FROM FavouritePlayer WHERE id = :id")
    suspend fun deleteFavouritePlayerById(id: Int)

    @Query("DELETE FROM FavouritePlayer")
    suspend fun deleteAllFavouritePlayers()

    @Query("SELECT MAX(dbOrderPosition) FROM FavouritePlayer")
    suspend fun getLastFavouritePlayerPosition(): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayerFavouriteImage(image: PlayerImage)

    @Query("SELECT * FROM PlayerImage WHERE playerId = :id")
    suspend fun getPlayerFavouriteImage(id: Int): PlayerImage?

    @Query("SELECT * FROM PlayerImage")
    suspend fun getAllFavouriteImages(): List<PlayerImage>?

    @Query("DELETE FROM PlayerImage WHERE playerId = :id")
    suspend fun deletePlayerFavouriteImage(id: Int)
}