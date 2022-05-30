package com.example.myapplication.network.services

import com.example.myapplication.models.PlayerImagePost
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
}