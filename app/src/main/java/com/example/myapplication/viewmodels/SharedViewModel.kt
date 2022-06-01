package com.example.myapplication.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.example.myapplication.database.NBAAppDatabase
import com.example.myapplication.models.*
import com.example.myapplication.network.NetworkRepo
import com.example.myapplication.network.models.PlayerImagesResponse
import com.example.myapplication.network.paging.PlayersRemoteMediator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import retrofit2.Response

private const val PLAYER_PAGE_SIZE = 20
private const val PLAYER_MAX_PAGE_SIZE = 100

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

    fun getPlayersByName(search: String) {
        viewModelScope.launch {
            val response = NetworkRepo().getPlayersByName(null, PLAYER_MAX_PAGE_SIZE, search)
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
                null,
                1,
                arrayOf(playerId),
                false
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

    fun getAllFavouriteImages(context: Context) {
        viewModelScope.launch {
            allFavouriteImages.value =
                NBAAppDatabase.getDatabase(context)?.playersDao()?.getAllFavouriteImages()
                    ?: listOf()
        }
    }
}
