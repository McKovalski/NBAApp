package com.example.myapplication.network

import com.example.myapplication.network.services.NBAService
import com.example.myapplication.network.services.SofaScoreService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Network {

    private val service: NBAService
    private val sofaService: SofaScoreService
    private val nbaBaseUrl = "https://www.balldontlie.io/api/v1/"
    private val sofaBaseUrl = "https://academy-2022.dev.sofascore.com/api/v1/academy/"

    init {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BASIC
        val httpClient = OkHttpClient.Builder().addInterceptor(interceptor)
        var retrofit = Retrofit.Builder()
            .baseUrl(nbaBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build()
        service = retrofit.create(NBAService::class.java)
        retrofit = Retrofit.Builder()
            .baseUrl(sofaBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build()
        sofaService = retrofit.create(SofaScoreService::class.java)
    }

    fun getNbaService(): NBAService = service
    fun getSofaScoreService(): SofaScoreService = sofaService
}