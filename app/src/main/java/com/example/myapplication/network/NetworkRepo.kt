package com.example.myapplication.network

import com.example.myapplication.network.models.*
import retrofit2.Response

class NetworkRepo {

    suspend fun getPlayers(page: Int?, perPage: Int?): PlayerResponse {
        return Network().getNbaService().getPlayers(page, perPage)
    }

    suspend fun getTeams(): TeamsResponse {
        return Network().getNbaService().getTeams()
    }

    suspend fun getSeasonAveragesForPlayer(
        season: Int?,
        playerIds: Array<Int>
    ): SeasonAveragesResponse {
        return Network().getNbaService().getSeasonAveragesForPlayer(season, playerIds)
    }

    suspend fun getStatsForPlayer(
        page: Int?,
        perPage: Int?,
        playerIds: Array<Int>,
        postseason: Boolean?
    ): StatsResponse {
        return Network().getNbaService().getStatsForPlayer(page, perPage, playerIds, postseason)
    }

    suspend fun getPlayerImages(playerId: Int): Response<PlayerImagesResponse> {
        return Network().getSofaScoreService().getPlayerImages(playerId)
    }
}