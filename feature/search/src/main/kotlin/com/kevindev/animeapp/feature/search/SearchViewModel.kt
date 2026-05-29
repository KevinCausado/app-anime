package com.kevindev.animeapp.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.apollographql.apollo.ApolloClient
import com.kevindev.animeapp.core.model.Anime
import com.kevindev.animeapp.core.network.graphql.type.MediaFormat
import com.kevindev.animeapp.core.network.graphql.type.MediaStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class SearchFilters(
    val genre: String? = null,
    val year: Int? = null,
    val status: MediaStatus? = null,
    val format: MediaFormat? = null,
)

data class SearchUiState(
    val query: String = "",
    val filters: SearchFilters = SearchFilters(),
    val showFilters: Boolean = false,
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val apolloClient: ApolloClient,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val results: Flow<PagingData<Anime>> = _uiState
        .debounce { state ->
            if (state.query.isBlank()) 0L else 300L
        }
        .flatMapLatest { state ->
            val effectiveQuery = state.query.takeIf { it.length >= 3 }
            Pager(PagingConfig(pageSize = 20, prefetchDistance = 5)) {
                SearchPagingSource(
                    apolloClient = apolloClient,
                    query = effectiveQuery,
                    filters = state.filters,
                )
            }.flow
        }
        .cachedIn(viewModelScope)

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    fun onFiltersChange(filters: SearchFilters) {
        _uiState.update { it.copy(filters = filters, showFilters = false) }
    }

    fun toggleFilters() {
        _uiState.update { it.copy(showFilters = !it.showFilters) }
    }

    fun clearQuery() {
        _uiState.update { it.copy(query = "") }
    }
}
