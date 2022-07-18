package com.formaloo.home.vm


import android.util.ArrayMap
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.formaloo.common.Constants.SHAREDBOARDADDRESS
import com.formaloo.common.base.BaseViewModel
import com.formaloo.model.Result
import com.formaloo.model.boards.block.Block
import com.formaloo.model.form.Fields
import com.formaloo.model.local.Restaurant
import com.formaloo.repository.board.BoardRepo
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber


/**
 * UI state for the Home screen
 */
data class RestaurantUiState(
    val restaurantBlock: Block? = null,
    val imageField: Fields? = null,
    val titleField: Fields? = null,
    val loading: Boolean = false,
    val errorMessages: List<String> = emptyList()
) {
    /**
     * True if this represents a first load
     */
    val initialLoad: Boolean
        get() = imageField == null && titleField == null && errorMessages.isEmpty() && loading
}


class RestaurantViewModel(private val repository: BoardRepo) : BaseViewModel() {


    // UI state exposed to the UI
    private val _uiState = MutableStateFlow(RestaurantUiState(loading = true))
    val uiState: StateFlow<RestaurantUiState> = _uiState.asStateFlow()

    fun fetchRestaurantBlock(blockSlug: String) {
        // Ui state is refreshing
        _uiState.update { it.copy(loading = true) }

        viewModelScope.launch {
            val result = repository.getBlock(SHAREDBOARDADDRESS, blockSlug)
            _uiState.update {
                when (result) {
                    is Result.Success -> {
                        Timber.e("refreshRestaurantBlock ${result.data.data}")
                        val block = result.data.data?.block
                        it.copy(
                            restaurantBlock = block,
                            imageField = block?.featured_image_field,
                            titleField = block?.items_field,
                            loading = false
                        )
                    }
                    is Result.Error -> {
                        val errorMessages = it.errorMessages
                        it.copy(errorMessages = errorMessages, loading = false)
                    }

                }
            }
        }
    }

    fun fetchRestaurantMenu(blockSlug: String) {
        // Ui state is refreshing
        _uiState.update { it.copy(loading = true) }

        viewModelScope.launch {
            val result = repository.getBlock(SHAREDBOARDADDRESS, blockSlug)
            when (result) {
                is Result.Success -> {
                    val restaurantMenu = result.data.data?.block
                    restaurantMenu?.items?.let { blockList ->
                        if (blockList.isNotEmpty()) {
                            val restaurantBlock = blockList[0].block
                            fetchRestaurantBlock(restaurantBlock?.slug?:"")
                        }
                    }

                }
                is Result.Error -> {

                }

            }

        }
    }

    fun fetchBlock(blockSlug: String) {
        fetchRestaurantBlock(blockSlug)
    }

    fun fetchRestaurantList(blockSlug: String, force: Boolean): Flow<PagingData<Restaurant>> {
        val params = ArrayMap<String, Any>()
        return repository.fetchRestaurants(SHAREDBOARDADDRESS, blockSlug, force, params)
            .cachedIn(viewModelScope)
    }
}
