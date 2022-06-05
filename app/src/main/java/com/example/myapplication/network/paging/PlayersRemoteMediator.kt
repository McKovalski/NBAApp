package com.example.myapplication.network.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.myapplication.database.NBAAppDatabase
import com.example.myapplication.models.Player
import com.example.myapplication.models.PlayerRemoteKey
import com.example.myapplication.network.NetworkRepo

@ExperimentalPagingApi
class PlayersRemoteMediator(
    private val initialPage: Int = 1,
    private val db: NBAAppDatabase,
    private val api: NetworkRepo
) : RemoteMediator<Int, Player>() {

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Player>): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                initialPage
            }
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val remoteKey = getRemoteKeyForLastItem(state)
                remoteKey?.nextKey ?: return MediatorResult.Success(endOfPaginationReached = true)
            }
        }

        val response = api.getPlayers(page, state.config.pageSize)
        val players = response.data
        val endOfPaginationReached = response.meta.next_page == null

        db.withTransaction {
            // If refreshing, clear table and start over
            /*if (loadType == LoadType.REFRESH) {
                db.playerRemoteKeyDao().clearAllKeys()
                db.playersDao().clearAll()
            }*/
            val prevKey = if (page == initialPage) null else page - 1
            val nextKey = if (endOfPaginationReached) null else page + 1
            val remoteKeys = players.map {
                PlayerRemoteKey(it.id, prevKey, nextKey)
            }
            db.playerRemoteKeyDao().insertAll(remoteKeys)
            db.playersDao().insertAll(players)
        }
        return MediatorResult.Success(endOfPaginationReached)
    }


    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Player>): PlayerRemoteKey? {
        return db.playerRemoteKeyDao().getLastRemoteKey()
    }

    /*private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, Player>): PlayerRemoteKey? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                db.withTransaction { db.playerRemoteKeyDao().remoteKeyByPlayerId(id) }
            }
        }
    }*/
}