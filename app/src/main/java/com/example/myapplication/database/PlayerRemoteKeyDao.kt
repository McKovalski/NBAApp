package com.example.myapplication.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myapplication.models.PlayerRemoteKey

@Dao
interface PlayerRemoteKeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKeys: List<PlayerRemoteKey>)

    @Query("SELECT * FROM PlayerRemoteKey WHERE id == :playerId")
    suspend fun remoteKeyByPlayerId(playerId: Int): PlayerRemoteKey?

    @Query("DELETE FROM PlayerRemoteKey")
    suspend fun clearAllKeys()

    @Query("SELECT * FROM PlayerRemoteKey ORDER BY nextKey DESC LIMIT 1")
    suspend fun getLastRemoteKey(): PlayerRemoteKey?
}