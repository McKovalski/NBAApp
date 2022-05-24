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
import com.example.myapplication.network.paging.PlayersRemoteMediator
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

private const val PLAYER_PAGE_SIZE = 20

class SharedViewModel : ViewModel() {

    val allTeams = MutableLiveData<List<Team>>()
    val favouriteTeams = MutableLiveData<List<Team>>()
    val lastFavouriteTeamPosition = MutableLiveData<Int>()
    val favouritePlayers = MutableLiveData<List<Player>>()
    val lastFavouritePlayerPosition = MutableLiveData<Int>()

    val spinnerSelectedPosition = MutableLiveData<Int>()
    val playersLastSeason = MutableLiveData<Int>()
    val seasonAveragesForPlayer = MutableLiveData<List<SeasonAverages>>()

    @ExperimentalPagingApi
    fun getPlayerPaginatedFlow(context: Context): Flow<PagingData<Player>> {
        val database = NBAAppDatabase.getDatabase(context)!!
        return Pager(
            config = PagingConfig(PLAYER_PAGE_SIZE, enablePlaceholders = true),
            remoteMediator = PlayersRemoteMediator(
                1,
                NBAAppDatabase.getDatabase(context)!!,
                NetworkRepo()
            )
        ) {
            database.playersDao().pagingSource()
        }.flow.cachedIn(viewModelScope)
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

    fun getLastFavouriteTeamPosition(context: Context) {
        viewModelScope.launch {
            lastFavouriteTeamPosition.value =
                NBAAppDatabase.getDatabase(context)?.teamsDao()?.getLastFavouriteTeamPosition() ?: 0
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

    fun getLastFavouritePlayerPosition(context: Context) {
        viewModelScope.launch {
            lastFavouritePlayerPosition.value =
                NBAAppDatabase.getDatabase(context)?.playersDao()?.getLastFavouritePlayerPosition()
                    ?: 0
        }
    }

    fun getPlayersLastSeason(playerId: Int) {
        viewModelScope.launch {
            playersLastSeason.value =
                NetworkRepo().getSeasonAveragesForPlayer(null, arrayOf(playerId)).data[0].season
            Log.d("Last season", playersLastSeason.value.toString())
        }
    }

    fun getSeasonAveragesForPlayer(playerId: Int, season: Int?) {
        viewModelScope.launch {
            //getPlayersLastSeason(playerId)
            val lastSeason = 2021
            // list of seasons to show
            val seasons = mutableListOf(
                lastSeason,
                lastSeason.minus(1),
                lastSeason.minus(2),
                lastSeason.minus(3)
            )
            // if one of the seasons is our season, then we move it to the start
            // if it's not, then we remove the last season and prepend ours
            if (season != null) {
                if (season in seasons) {
                    seasons.remove(season)
                } else {
                    seasons.removeLast()
                }
                seasons.add(0, season)
            }
            val seasonAverages = seasons.map { s ->
                async {
                    NetworkRepo().getSeasonAveragesForPlayer(s, arrayOf(playerId)).data[0]
                }
            }
            seasonAveragesForPlayer.value = seasonAverages.awaitAll()
        }
    }
}
