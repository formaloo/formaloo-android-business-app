package com.formaloo.home.vm

import androidx.lifecycle.viewModelScope
import com.formaloo.common.Constants.SHAREDBOARDADDRESS
import com.formaloo.common.base.BaseViewModel
import com.formaloo.model.Result
import com.formaloo.model.form.Form
import com.formaloo.repository.FormzRepo
import com.formaloo.repository.board.BoardRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber


/**
 * UI state for the Home screen
 */
data class CareerUiState(
    val careerForm: Form? = null,
    val loading: Boolean = false,
    val errorMessages: List<String> = emptyList()
) {
    /**
     * True if this represents a first load
     */
    val initialLoad: Boolean
        get() = errorMessages.isEmpty() && loading
}


class CareerViewModel(private val repository: BoardRepo, private val formRepo: FormzRepo) :
    BaseViewModel() {

    // UI state exposed to the UI
    private val _uiState = MutableStateFlow(CareerUiState(loading = true))
    val uiState: StateFlow<CareerUiState> = _uiState.asStateFlow()


    fun refreshCareer(blockSlug: String) {
        // Ui state is refreshing
        _uiState.update { it.copy(loading = true) }

        viewModelScope.launch {
            val result = repository.getBlock(SHAREDBOARDADDRESS, blockSlug)

            when (result) {
                is Result.Success -> {
                    Timber.e("Career ${result.data.data?.block}")
                    fetchForm(result.data.data?.block?.form?.address ?: "")

                }
                is Result.Error -> {
                }

            }
        }
    }

    fun fetchBlock(blockSlug: String) {
        refreshCareer(blockSlug)
    }

    fun fetchForm(formAddress: String) {
        // Ui state is refreshing
        _uiState.update { it.copy(loading = true) }

        viewModelScope.launch {
            val result = formRepo.displayForm(formAddress)
            _uiState.update {
                when (result) {
                    is Result.Success -> {
                        it.copy(
                            careerForm = result.data.data?.form,
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


}
