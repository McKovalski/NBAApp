package com.example.myapplication.network

import com.example.myapplication.network.models.PlayerResponse
import com.example.myapplication.network.models.SeasonAveragesResponse
import com.example.myapplication.network.models.TeamsResponse

class NetworkRepo {

    suspend fun getPlayers(page: Int?, perPage: Int?): PlayerResponse {
        return Network().getService().getPlayers(page, perPage)
    }

    suspend fun getTeams(): TeamsResponse {
        return Network().getService().getTeams()
    }

    suspend fun getSeasonAveragesForPlayer(season: Int?, playerIds: Array<Int>): SeasonAveragesResponse {
        return Network().getService().getSeasonAveragesForPlayer(season, playerIds)
    }
}