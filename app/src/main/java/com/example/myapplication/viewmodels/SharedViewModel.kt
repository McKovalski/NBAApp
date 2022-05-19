package com.example.myapplication.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.example.myapplication.database.NBAAppDatabase
import com.example.myapplication.models.Player
import com.example.myapplication.network.NetworkRepo
import com.example.myapplication.network.paging.PlayersRemoteMediator
import kotlinx.coroutines.flow.Flow

private const val PLAYER_PAGE_SIZE = 20

class SharedViewModel : ViewModel() {

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
}
