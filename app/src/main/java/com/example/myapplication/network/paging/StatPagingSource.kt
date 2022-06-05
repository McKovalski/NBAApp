package com.example.myapplication.network.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.myapplication.helpers.Constants
import com.example.myapplication.models.Stats
import com.example.myapplication.network.services.NBAService
import kotlinx.coroutines.runBlocking
import java.lang.Exception

class StatPagingSource(
    private val service: NBAService,
    private val isPostseason: Boolean? = null,
    private val playerIds: Array<Int>,
    private val seasons: Array<Int>? = arrayOf(Constants().LAST_SEASON)
) : PagingSource<Int, Stats>() {

    private var lastPage = -1

    override fun getRefreshKey(state: PagingState<Int, Stats>): Int? {
        return state.anchorPosition?.let {
            val anchorPage = state.closestPageToPosition(it)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Stats> {
        if (lastPage == -1) {
            runBlocking {
                lastPage = service.getStatsForPlayer(
                    page = 1,
                    perPage = params.loadSize,
                    postseason = isPostseason,
                    playerIds = playerIds,
                    seasons = seasons
                ).meta.total_pages
            }
        }
        return try {
            val nextPage = params.key ?: lastPage
            val response = service.getStatsForPlayer(
                page = nextPage,
                perPage = params.loadSize,
                postseason = isPostseason,
                playerIds = playerIds,
                seasons = seasons
            )
            LoadResult.Page(
                data = response.data,
                prevKey = null,
                nextKey = if (response.meta.current_page - 1 > 0) response.meta.current_page - 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}