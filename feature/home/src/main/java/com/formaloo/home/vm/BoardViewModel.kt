package com.formaloo.home.ui


import androidx.lifecycle.viewModelScope
import com.formaloo.common.base.BaseViewModel
import com.formaloo.model.Result
import com.formaloo.model.boards.board.Board
import com.formaloo.repository.board.BoardRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


/**
 * UI state for the Home screen
 */
data class BoardUiState(
    val board: Board? = null,
    val loading: Boolean = false,
    val errorMessages: List<String> = emptyList()
) {
    /**
     * True if this represents a first load
     */
    val initialLoad: Boolean
        get() = errorMessages.isEmpty() && loading
}


class BoardViewModel(private val repository: BoardRepo) : BaseViewModel() {

    // UI state exposed to the UI
    private val _uiState = MutableStateFlow(BoardUiState(loading = true))
    val uiState: StateFlow<BoardUiState> = _uiState.asStateFlow()

    fun refreshBoard(boardAddress: String) {
        // Ui state is refreshing
        _uiState.update { it.copy(loading = true) }


        viewModelScope.launch {
            repository.getBoardData(boardAddress)?.let {board->
                _uiState.update {
                    it.copy(
                        board = board,
                        loading = false
                    )
                }
            }

            val result = repository.getBoard(boardAddress)
            _uiState.update {

                when (result) {
                    is Result.Success -> {
                        val board = result.data.data?.board
                        board?.let {
                            saveBoard(board)
                        }

                        it.copy(
                            board = board,
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

    private fun saveBoard(board: Board) = viewModelScope.launch {
        repository.saveBoard(board)


    }


}
