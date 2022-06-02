package com.example.myapplication.network.services

import com.example.myapplication.network.models.*
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
    suspend fun getTeams(): TeamsResponse

    @GET("season_averages")
    suspend fun getSeasonAveragesForPlayer(
        @Query("season") season: Int?,
        @Query("player_ids[]") playerIds: Array<Int>
    ): SeasonAveragesResponse

    @GET("stats")
    suspend fun getStatsForPlayer(
        @Query("page") page: Int?,
        @Query("per_page") perPage: Int?,
        @Query("player_ids[]") playerIds: Array<Int>,
        @Query("postseason") postseason: Boolean?
    ): StatsResponse

    @GET("stats")
    suspend fun getStatsForGame(
        @Query("page") page: Int?,
        @Query("per_page") perPage: Int?,
        @Query("game_ids[]") gameIds: Array<Int>,
        @Query("postseason") postseason: Boolean?
    ): StatsResponse

    @GET("games")
    suspend fun getMatches(
        @Query("page") page: Int?,
        @Query("per_page") perPage: Int?,
        @Query("postseason") postseason: Boolean?,
        @Query("team_ids[]") teamIds: Array<Int>?,
        @Query("seasons[]") seasons: Array<Int>?,
    ): MatchesResponse
}