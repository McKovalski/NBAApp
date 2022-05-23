package com.example.myapplication.network

import com.example.myapplication.models.PlayerResponse
import com.example.myapplication.models.TeamsResponse

class NetworkRepo {

    suspend fun getPlayers(page: Int, perPage: Int): PlayerResponse {
        return Network().getService().getPlayers(page, perPage)
    }

    suspend fun getTeams() : TeamsResponse {
        return Network().getService().getTeams()
    }
}