package com.example.myapplication.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myapplication.models.FavouriteTeam
import com.example.myapplication.models.Team

@Dao
interface TeamsDao {

    @Query("SELECT * FROM Team ORDER BY full_name")
    suspend fun getAllTeams(): List<Team>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTeams(teams: List<Team>)

    @Query("SELECT * FROM FavouriteTeam ORDER BY dbOrderPosition")
    suspend fun getAllFavouriteTeams(): List<FavouriteTeam>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavouriteTeam(favouriteTeam: FavouriteTeam)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFavouriteTeams(favouriteTeams: List<FavouriteTeam>)

    @Query("DELETE FROM FavouriteTeam WHERE team_id = :teamId")
    suspend fun deleteFavouriteTeamById(teamId: Int)

    @Query("DELETE FROM FavouriteTeam")
    suspend fun deleteAllFavouriteTeams()

    @Query("SELECT MAX(dbOrderPosition) FROM FavouriteTeam")
    suspend fun getLastFavouriteTeamPosition(): Int?
}