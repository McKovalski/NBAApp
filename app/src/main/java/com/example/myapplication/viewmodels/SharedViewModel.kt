package com.example.myapplication.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.example.myapplication.database.NBAAppDatabase
import com.example.myapplication.models.FavouritePlayer
import com.example.myapplication.models.FavouriteTeam
import com.example.myapplication.models.Player
import com.example.myapplication.models.Team
import com.example.myapplication.network.NetworkRepo
import com.example.myapplication.network.paging.PlayersRemoteMediator
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
            NBAAppDatabase.getDatabase(context)?.playersDao()?.insertFavouritePlayer(favouritePlayer)
            getFavouritePlayers(context)
        }
    }

    fun addAllFavouritePlayers(context: Context, favouritePlayers: List<FavouritePlayer>) {
        viewModelScope.launch {
            NBAAppDatabase.getDatabase(context)?.playersDao()?.insertAllFavouritePlayers(favouritePlayers)
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
                NBAAppDatabase.getDatabase(context)?.playersDao()?.getLastFavouritePlayerPosition() ?: 0
        }
    }
}
