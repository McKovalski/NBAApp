package com.example.myapplication.network

import com.example.myapplication.models.HighlightPost
import com.example.myapplication.models.Match
import com.example.myapplication.models.PlayerImagePost
import com.example.myapplication.network.models.*
import retrofit2.Response

class NetworkRepo {

    suspend fun getPlayers(page: Int? = null, perPage: Int? = null): PlayerResponse {
        return Network().getNbaService().getPlayers(page, perPage)
    }

    suspend fun getPlayersByName(
        page: Int? = null,
        perPage: Int? = null,
        search: String
    ): PlayerResponse {
        return Network().getNbaService().getPlayersByName(page, perPage, search)
    }

    suspend fun getTeams(): TeamsResponse {
        return Network().getNbaService().getTeams()
    }

    suspend fun getSeasonAveragesForPlayer(
        season: Int? = null,
        playerIds: Array<Int>
    ): SeasonAveragesResponse {
        return Network().getNbaService().getSeasonAveragesForPlayer(season, playerIds)
    }

    suspend fun getStatsForPlayer(
        page: Int? = null,
        perPage: Int? = null,
        playerIds: Array<Int>,
        postseason: Boolean? = null,
        seasons: Array<Int>? = null
    ): StatsResponse {
        return Network().getNbaService()
            .getStatsForPlayer(page, perPage, playerIds, postseason, seasons)
    }

    suspend fun getStatsForGame(
        page: Int? = null,
        perPage: Int? = null,
        gameIds: Array<Int>,
        postseason: Boolean? = null
    ): StatsResponse {
        return Network().getNbaService().getStatsForGame(page, perPage, gameIds, postseason)
    }

    suspend fun getPlayerImages(playerId: Int): Response<PlayerImagesResponse> {
        return Network().getSofaScoreService().getPlayerImages(playerId)
    }

    suspend fun postPlayerImage(image: PlayerImagePost): Response<Unit> {
        return Network().getSofaScoreService().postPlayerImage(image)
    }

    suspend fun deletePlayerImageById(id: Int): Response<Unit> {
        return Network().getSofaScoreService().deletePlayerImageById(id)
    }

    suspend fun getEventHighlights(eventId: Int): Response<HighlightsResponse> {
        return Network().getSofaScoreService().getEventHighlights(eventId)
    }

    suspend fun getPlayerHighlights(playerId: Int): Response<HighlightsResponse> {
        return Network().getSofaScoreService().getPlayerHighlights(playerId)
    }

    suspend fun postHighlight(highlight: HighlightPost): Response<Unit> {
        return Network().getSofaScoreService().postHighlight(highlight)
    }

    suspend fun deleteHighlightById(id: Int): Response<Unit>  {
        return Network().getSofaScoreService().deleteHighlightById(id)
    }

    suspend fun getMatchById(id: Int): Match {
        return Network().getNbaService().getMatchById(id)
    }
}