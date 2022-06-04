package com.example.myapplication.network.services

import com.example.myapplication.models.HighlightPost
import com.example.myapplication.models.PlayerImagePost
import com.example.myapplication.network.models.HighlightsResponse
import com.example.myapplication.network.models.PlayerImagesResponse
import retrofit2.Response
import retrofit2.http.*

interface SofaScoreService {

    @GET("player-image/player/{playerId}")
    suspend fun getPlayerImages(@Path("playerId") playerId: Int): Response<PlayerImagesResponse>

    @POST("player-image")
    suspend fun postPlayerImage(@Body image: PlayerImagePost): Response<Unit>

    @DELETE("player-image/{id}")
    suspend fun deletePlayerImageById(@Path("id") id: Int)

    @GET("highlight/event/{eventId}")
    suspend fun getEventHighlights(@Path("eventId") eventId: Int): Response<HighlightsResponse>

    @GET("highlight/player/{playerId}")
    suspend fun getPlayerHighlights(@Path("playerId") playerId: Int): Response<HighlightsResponse>

    @POST("highlight")
    suspend fun postHighlight(@Body highlight: HighlightPost): Response<Unit>

    @DELETE("highlight/{id}")
    suspend fun deleteHighlightById(@Path("id") id: Int): Response<Unit>
}