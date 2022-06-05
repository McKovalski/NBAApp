package com.example.myapplication.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.example.myapplication.database.NBAAppDatabase
import com.example.myapplication.helpers.Constants
import com.example.myapplication.models.*
import com.example.myapplication.network.Network
import com.example.myapplication.network.NetworkRepo
import com.example.myapplication.network.paging.MatchPagingSource
import com.example.myapplication.network.paging.PlayersRemoteMediator
import com.example.myapplication.network.paging.StatPagingSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private const val PLAYER_PAGE_SIZE = 20
private const val PLAYER_MAX_PAGE_SIZE = 100
private const val MATCH_PAGE_SIZE = 100

class SharedViewModel : ViewModel() {

    val allTeams = MutableLiveData<List<Team>>()
    val favouriteTeams = MutableLiveData<List<Team>>()
    val favouritePlayers = MutableLiveData<List<Player>>()
    val playersByName = MutableLiveData<List<Player>>()

    val spinnerSelectedPosition = MutableLiveData<Int>()
    val playerSeasons = MutableLiveData<List<Int>>()
    val seasonAveragesForPlayer = MutableLiveData<SeasonAverages?>()

    val playerImages = MutableLiveData<MutableList<PlayerImage>>()
    val playerFavouriteImage = MutableLiveData<PlayerImage>()
    val allFavouriteImages = MutableLiveData<List<PlayerImage>>()

    val playerHighlights = MutableLiveData<List<Highlight>>()
    val eventHighlights = MutableLiveData<List<Highlight>>()

    val statsForGame = MutableLiveData<List<Stats>>()

    @ExperimentalPagingApi
    fun getPlayerPaginatedFlow(context: Context): Flow<PagingData<Player>> {
        val database = NBAAppDatabase.getDatabase(context)!!
        return Pager(
            config = PagingConfig(
                PLAYER_PAGE_SIZE,
                enablePlaceholders = true,
                initialLoadSize = 5 * PLAYER_PAGE_SIZE
            ),
            remoteMediator = PlayersRemoteMediator(
                1,
                NBAAppDatabase.getDatabase(context)!!,
                NetworkRepo()
            )
        ) {
            database.playersDao().pagingSource()
        }.flow.cachedIn(viewModelScope)
    }

    fun getAllMatchesFlow(
        postseason: Boolean,
        teamIds: Array<Int>? = null,
        seasons: Array<Int>? = arrayOf(Constants().LAST_SEASON)
    ): Flow<PagingData<Match>> {
        return Pager(config = PagingConfig(MATCH_PAGE_SIZE)) {
            MatchPagingSource(Network().getNbaService(), postseason, teamIds, seasons)
        }.flow.cachedIn(viewModelScope)
    }

    fun getMatchesFlowOpponentFilter(
        postseason: Boolean,
        teamIds: Array<Int>? = null,
        seasons: Array<Int>? = arrayOf(Constants().LAST_SEASON),
        opponent: Team? = null
    ): Flow<PagingData<Match>> {
        return if (opponent != null) {
            Pager(config = PagingConfig(MATCH_PAGE_SIZE)) {
                MatchPagingSource(Network().getNbaService(), postseason, teamIds, seasons)
            }.flow
                .map { pagingData ->
                    pagingData.filter { match ->
                        match.home_team.id == opponent.id || match.visitor_team.id == opponent.id
                    }
                }
                .cachedIn(viewModelScope)
        } else getAllMatchesFlow(postseason, teamIds, seasons)
    }

    fun getStatsForPlayerFlow(
        postseason: Boolean,
        playerIds: Array<Int>,
        seasons: Array<Int>? = arrayOf(Constants().LAST_SEASON)
    ): Flow<PagingData<Stats>> {
        return Pager(config = PagingConfig(MATCH_PAGE_SIZE)) {
            StatPagingSource(Network().getNbaService(), postseason, playerIds, seasons)
        }.flow.cachedIn(viewModelScope)
    }

    fun getPlayersByName(search: String) {
        viewModelScope.launch {
            val response =
                NetworkRepo().getPlayersByName(perPage = PLAYER_MAX_PAGE_SIZE, search = search)
            playersByName.value = response.data
        }
    }

    private fun setAllTeams(context: Context) {
        viewModelScope.launch {
            val teamsResponse = NetworkRepo().getTeams()
            val teams = teamsResponse.data
            NBAAppDatabase.getDatabase(context)?.teamsDao()?.insertAllTeams(teams)
            allTeams.value = teams
        }
    }

    fun getAllTeams(context: Context) {
        viewModelScope.launch {
            val teams = NBAAppDatabase.getDatabase(context)?.teamsDao()?.getAllTeams()
            if (teams.isNullOrEmpty()) {
                setAllTeams(context)
            } else {
                allTeams.value = teams!!
            }
        }
    }

    fun getFavouriteTeams(context: Context) {
        viewModelScope.launch {
            val teams =
                NBAAppDatabase.getDatabase(context)?.teamsDao()?.getAllFavouriteTeams()
            favouriteTeams.value = teams?.map { favouriteTeam ->
                favouriteTeam.toTeam()
            }
        }
    }

    fun addFavouriteTeam(context: Context, favouriteTeam: FavouriteTeam) {
        viewModelScope.launch {
            NBAAppDatabase.getDatabase(context)?.teamsDao()?.insertFavouriteTeam(favouriteTeam)
            getFavouriteTeams(context)
        }
    }

    fun addAllFavouriteTeams(context: Context, favouriteTeams: List<FavouriteTeam>) {
        viewModelScope.launch {
            NBAAppDatabase.getDatabase(context)?.teamsDao()?.insertAllFavouriteTeams(favouriteTeams)
            getFavouriteTeams(context)
        }
    }

    fun removeFavouriteTeam(context: Context, teamId: Int) {
        viewModelScope.launch {
            NBAAppDatabase.getDatabase(context)?.teamsDao()?.deleteFavouriteTeamById(teamId)
            getFavouriteTeams(context)
        }
    }

    fun removeAllFavouriteTeams(context: Context) {
        viewModelScope.launch {
            NBAAppDatabase.getDatabase(context)?.teamsDao()?.deleteAllFavouriteTeams()
            getFavouriteTeams(context)
        }
    }

    fun getFavouritePlayers(context: Context) {
        viewModelScope.launch {
            val players =
                NBAAppDatabase.getDatabase(context)?.playersDao()?.getAllFavouritePlayers()
            favouritePlayers.value = players?.map { favouritePlayer ->
                favouritePlayer.toPlayer()
            }
        }
    }

    fun addFavouritePlayer(context: Context, favouritePlayer: FavouritePlayer) {
        viewModelScope.launch {
            NBAAppDatabase.getDatabase(context)?.playersDao()
                ?.insertFavouritePlayer(favouritePlayer)
            getFavouritePlayers(context)
        }
    }

    fun addAllFavouritePlayers(context: Context, favouritePlayers: List<FavouritePlayer>) {
        viewModelScope.launch {
            NBAAppDatabase.getDatabase(context)?.playersDao()
                ?.insertAllFavouritePlayers(favouritePlayers)
            getFavouritePlayers(context)
        }
    }

    fun removeFavouritePlayer(context: Context, id: Int) {
        viewModelScope.launch {
            NBAAppDatabase.getDatabase(context)?.playersDao()?.deleteFavouritePlayerById(id)
            getFavouritePlayers(context)
        }
    }

    fun removeAllFavouritePlayers(context: Context) {
        viewModelScope.launch {
            NBAAppDatabase.getDatabase(context)?.playersDao()?.deleteAllFavouritePlayers()
            getFavouritePlayers(context)
        }
    }

    fun getPlayerSeasons(playerId: Int) {
        Log.d("Player id", playerId.toString())
        viewModelScope.launch {
            val firstSeasonResponse = NetworkRepo().getStatsForPlayer(
                perPage = 1,
                playerIds = arrayOf(playerId),
                postseason = false
            )
            val firstSeason = firstSeasonResponse.data?.get(0)?.game?.season
            val lastPage = firstSeasonResponse.meta.total_pages
            val lastSeason = NetworkRepo().getStatsForPlayer(
                lastPage,
                1,
                arrayOf(playerId),
                false
            ).data?.get(0)?.game?.season
            if (firstSeason == null && lastSeason == null) {
                playerSeasons.value = listOf()
            } else if (firstSeason != null && lastSeason == null) {
                playerSeasons.value = listOf(firstSeason)
            } else if (firstSeason != null && lastSeason != null) {
                playerSeasons.value = (firstSeason..lastSeason).map { it }
            }
        }
    }

    fun getSeasonAveragesForPlayer(playerId: Int, season: Int?) {
        viewModelScope.launch {
            val response = NetworkRepo().getSeasonAveragesForPlayer(season, arrayOf(playerId))
            if (!response.data.isNullOrEmpty()) {
                seasonAveragesForPlayer.value = response.data[0]
            } else {
                seasonAveragesForPlayer.value = null
            }
        }
    }

    fun getPlayerImages(playerId: Int) {
        viewModelScope.launch {
            val response = NetworkRepo().getPlayerImages(playerId)
            val currentImages = playerImages.value ?: mutableListOf()
            if (response.isSuccessful) {
                currentImages.addAll(response.body()?.data!!)
                playerImages.value = currentImages
            }
        }
    }

    fun postPlayerImage(playerId: Int, imageUrl: String, imageCaption: String) {
        viewModelScope.launch {
            val image = PlayerImagePost(playerId, imageUrl, imageCaption)
            NetworkRepo().postPlayerImage(image)
            getPlayerImages(playerId)
        }
    }

    fun deletePlayerImage(image: PlayerImage) {
        viewModelScope.launch {
            NetworkRepo().deletePlayerImageById(image.id)
            getPlayerImages(image.playerId)
            playerImages.value?.remove(image)
        }
    }

    fun getPlayerFavouriteImage(context: Context, id: Int) {
        viewModelScope.launch {
            playerFavouriteImage.value =
                NBAAppDatabase.getDatabase(context)?.playersDao()?.getPlayerFavouriteImage(id)
        }
    }

    fun setPlayerFavouriteImage(context: Context, image: PlayerImage) {
        viewModelScope.launch {
            NBAAppDatabase.getDatabase(context)?.playersDao()?.insertPlayerFavouriteImage(image)
            getAllFavouriteImages(context)
        }
    }

    fun deletePlayerFavouriteImage(context: Context, image: PlayerImage) {
        viewModelScope.launch {
            NBAAppDatabase.getDatabase(context)?.playersDao()
                ?.deletePlayerFavouriteImage(image.playerId)
            getAllFavouriteImages(context)
        }
    }

    fun getAllFavouriteImages(context: Context) {
        viewModelScope.launch {
            allFavouriteImages.value =
                NBAAppDatabase.getDatabase(context)?.playersDao()?.getAllFavouriteImages()
                    ?: listOf()
        }
    }

    fun getStatsForGame(match: Match) {
        viewModelScope.launch {
            val gameIds = arrayOf(match.id)
            val response = NetworkRepo().getStatsForGame(
                perPage = PLAYER_MAX_PAGE_SIZE,
                gameIds = gameIds,
                postseason = match.postseason
            )
            statsForGame.value = response.data ?: listOf()
        }
    }

    fun getEventHighlights(eventId: Int) {
        viewModelScope.launch {
            val response = NetworkRepo().getEventHighlights(eventId)
            if (response.isSuccessful) {
                eventHighlights.value = response.body()?.data ?: listOf()
            }
        }
    }

    fun getPlayerHighlights(playerId: Int) {
        viewModelScope.launch {
            val response = NetworkRepo().getPlayerHighlights(playerId)
            if (response.isSuccessful) {
                playerHighlights.value = response.body()?.data ?: listOf()
            }
        }
    }

    fun postHighlight(
        eventId: Int,
        name: String,
        url: String,
        playerIdList: List<Int>? = null,
        startTimestamp: Int
    ) {
        viewModelScope.launch {
            val highlight = HighlightPost(eventId, name, url, playerIdList, startTimestamp)
            NetworkRepo().postHighlight(highlight)
        }
    }

    fun deleteHighlightById(id: Int) {
        viewModelScope.launch {
            NetworkRepo().deleteHighlightById(id)
        }
    }
}
