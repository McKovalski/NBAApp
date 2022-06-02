package com.example.myapplication.network.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.myapplication.models.Match
import com.example.myapplication.network.NetworkRepo
import com.example.myapplication.network.services.NBAService
import kotlinx.coroutines.runBlocking
import java.lang.Exception

private const val INITIAL_PAGE = 1

class MatchPagingSource(
    private val service: NBAService,
    private val isPostseason: Boolean? = null,
    private val teamIds: Array<Int>? = null
) : PagingSource<Int, Match>() {

    private var lastPage = -1

    override fun getRefreshKey(state: PagingState<Int, Match>): Int? {
        return state.anchorPosition?.let {
            val anchorPage = state.closestPageToPosition(it)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Match> {
        if (lastPage == -1) {
            runBlocking {
                lastPage = service.getMatches(
                    page = 1,
                    perPage = params.loadSize,
                    isPostseason,
                    teamIds
                ).meta.total_pages
            }
        }
        return try {
            val nextPage = params.key ?: lastPage
            val response = service.getMatches(
                page = nextPage,
                perPage = params.loadSize,
                isPostseason,
                teamIds
            )
            LoadResult.Page(
                data = response.data,
                prevKey = null,
                nextKey = response.meta.current_page - 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
