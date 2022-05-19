package com.example.myapplication.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myapplication.models.Player

@Dao
interface PlayersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(players: List<Player>)

    // TODO vidi jel treba order by last name
    @Query("SELECT * FROM Player ORDER BY last_name")
    fun pagingSource(): PagingSource<Int, Player>

    @Query("DELETE FROM Player")
    suspend fun clearAll()
}