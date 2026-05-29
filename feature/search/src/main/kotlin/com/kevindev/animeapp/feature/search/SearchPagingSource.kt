package com.kevindev.animeapp.feature.search

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.kevindev.animeapp.core.model.Anime
import com.kevindev.animeapp.core.network.apollo.toAnime
import com.kevindev.animeapp.core.network.graphql.SearchAnimeQuery
import com.kevindev.animeapp.core.network.graphql.type.MediaFormat
import com.kevindev.animeapp.core.network.graphql.type.MediaStatus

class SearchPagingSource(
    private val apolloClient: ApolloClient,
    private val query: String?,
    private val filters: SearchFilters,
) : PagingSource<Int, Anime>() {

    override fun getRefreshKey(state: PagingState<Int, Anime>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Anime> {
        val page = params.key ?: 1
        return try {
            val response = apolloClient.query(
                SearchAnimeQuery(
                    query = Optional.presentIfNotNull(query),
                    genre = Optional.presentIfNotNull(filters.genre),
                    year = Optional.presentIfNotNull(filters.year),
                    status = Optional.presentIfNotNull(filters.status),
                    format = Optional.presentIfNotNull(filters.format),
                    page = page,
                    perPage = params.loadSize,
                )
            ).execute()

            val pageData = response.data?.Page
            val items = pageData?.media?.mapNotNull { it?.animeFragment?.toAnime() } ?: emptyList()
            val hasNext = pageData?.pageInfo?.hasNextPage ?: false

            LoadResult.Page(
                data = items,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (hasNext) page + 1 else null,
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
