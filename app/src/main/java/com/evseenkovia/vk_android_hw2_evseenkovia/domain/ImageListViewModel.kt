package com.evseenkovia.vk_android_hw2_evseenkovia.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evseenkovia.vk_android_hw2_evseenkovia.ui.ImageListUiState
import com.evseenkovia.vk_android_hw2_evseenkovia.ui.ImageUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ImageListViewModel(
    private val repository: ImageRepository
) : ViewModel() {

    private val _state = MutableStateFlow<ImageListUiState>(ImageListUiState.Loading)
    val state: StateFlow<ImageListUiState> = _state.asStateFlow()

    private var currentPage = 0
    private val pageSize = 20
    private var isLoading = false

    init {
        loadInitial()
    }

    fun loadInitial() {
        viewModelScope.launch {
            _state.value = ImageListUiState.Loading
            isLoading = true

            val result = repository.loadPage(0, pageSize)
            isLoading = false

            result.onSuccess { items ->
                currentPage = 1
                _state.value = ImageListUiState.Content(
                    items = items,
                    isPaginationLoading = false
                )
            }.onFailure {
                _state.value = ImageListUiState.Error()
            }
        }
    }

    fun retry() {
        loadInitial()
    }

    fun loadMore() {
        val currentState = _state.value
        if (currentState !is ImageListUiState.Content) return
        if (isLoading) return

        viewModelScope.launch {
            isLoading = true

            _state.value = currentState.copy(isPaginationLoading = true)

            val result = repository.loadPage(currentPage, pageSize)
            isLoading = false

            result.onSuccess { newItems ->
                currentPage++
                _state.value = ImageListUiState.Content(
                    items = currentState.items + newItems,
                    isPaginationLoading = false
                )
            }.onFailure {
                // На пагинации ошибки просто скрываем лоадер
                _state.value = currentState.copy(isPaginationLoading = false)
            }
        }
    }

    fun onImageClick(item: ImageUi) {
        // уведомление будет реализовано позже
    }
}
