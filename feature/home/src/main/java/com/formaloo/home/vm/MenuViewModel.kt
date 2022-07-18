package com.formaloo.home.vm

import androidx.lifecycle.viewModelScope
import com.formaloo.common.Constants.SHAREDBOARDADDRESS
import com.formaloo.common.base.BaseViewModel
import com.formaloo.model.Result
import com.formaloo.model.form.Form
import com.formaloo.model.form.submitForm.SubmitedRow
import com.formaloo.repository.FormzRepo
import com.formaloo.repository.board.BoardRepo
import com.formaloo.repository.submit.SubmitRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


/**
 * UI state for the Home screen
 */
data class MenuUiState(
    val menuForm: Form? = null,
    val loading: Boolean = false,
    val errorMessages: List<String> = emptyList()
) {
    /**
     * True if this represents a first load
     */
    val initialLoad: Boolean
        get() = errorMessages.isEmpty() && loading
}

/**
 * UI state for the Home screen
 */
data class OrderUiState(
    val orderDetail: SubmitedRow? = null,
    val loading: Boolean = false,
    val errorMessages: List<String> = emptyList()
) {
    /**
     * True if this represents a first load
     */
    val initialLoad: Boolean
        get() = errorMessages.isEmpty() && loading
}


class MenuViewModel(
    private val repository: BoardRepo,
    private val formRepo: FormzRepo,
    private val submitRepo: SubmitRepo,
) :
    BaseViewModel() {

    // UI state exposed to the UI
    private val _uiState = MutableStateFlow(MenuUiState(loading = true))
    val uiState: StateFlow<MenuUiState> = _uiState.asStateFlow()

    // UI state exposed to the UI
    private val _orderUiState = MutableStateFlow(OrderUiState(loading = true))
    val orderUiState: StateFlow<OrderUiState> = _orderUiState.asStateFlow()


    fun refreshMenu(blockSlug: String) {
        // Ui state is refreshing
        _uiState.update { it.copy(loading = true) }


        viewModelScope.launch {
            repository.getBlockFromDB(blockSlug)?.let {
                fetchForm(it.form?.address ?: "")

            }

            val result = repository.getBlock(SHAREDBOARDADDRESS, blockSlug)

            when (result) {
                is Result.Success -> {
                    val block = result.data.data?.block
                    block?.let { repository.saveBlockToDB(it) }

                    fetchForm(block?.form?.address ?: "")

                }
                is Result.Error -> {
                }

            }
        }
    }

    fun fetchBlock(blockSlug: String) {
        refreshMenu(blockSlug)
    }

    fun fetchForm(formAddress: String) {
        // Ui state is refreshing
        _uiState.update { it.copy(loading = true) }

        viewModelScope.launch {

            repository.getFormData(formAddress)?.let { form ->
                _uiState.update {
                    it.copy(
                        menuForm = form,
                        loading = false
                    )
                }
            }

            val result = formRepo.displayForm(formAddress)
            _uiState.update {
                when (result) {
                    is Result.Success -> {
                        val form = result.data.data?.form
                        form?.let { it1 -> repository.saveForm(it1) }
                        it.copy(
                            menuForm = form,
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


    fun submitOrder(
        slug: String,
        req: HashMap<String, Int>,
    ) {

        _orderUiState.update { it.copy(loading = true) }


        viewModelScope.launch {
            val result = submitRepo.submitForm(slug, req)

            when (result) {
                is Result.Success -> {
                    val orderDetail = result.data.data?.row
                    _orderUiState.update { it.copy(orderDetail = orderDetail) }

                }
                is Result.Error -> {
                }

            }
        }

    }
}
