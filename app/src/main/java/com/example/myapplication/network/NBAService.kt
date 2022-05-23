package com.example.myapplication.network

import com.example.myapplication.models.Player
import com.example.myapplication.models.PlayerResponse
import com.example.myapplication.models.TeamsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NBAService {

    @GET("players")
    suspend fun getPlayers(
        @Query("page") page: Int?,
        @Query("per_page") perPage: Int?,
    ): PlayerResponse

    @GET("players")
    suspend fun getPlayersByName(
        @Query("page") page: Int?,
        @Query("per_page") perPage: Int?,
        @Query("search") search: String?
    ): PlayerResponse

    @GET("teams")
    suspend fun getTeams() : TeamsResponse
}