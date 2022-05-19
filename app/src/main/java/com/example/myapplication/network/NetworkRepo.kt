package com.example.myapplication.network

import com.example.myapplication.models.PlayerResponse

class NetworkRepo {

    suspend fun getPlayers(page: Int, perPage: Int): PlayerResponse {
        return Network().getService().getPlayers(page, perPage)
    }
}