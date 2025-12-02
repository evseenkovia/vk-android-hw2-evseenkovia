package com.evseenkovia.vk_android_hw2_evseenkovia.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evseenkovia.vk_android_hw2_evseenkovia.data.repository.ImageRepositoryImpl
import com.evseenkovia.vk_android_hw2_evseenkovia.ui.ImageListUiState
import com.evseenkovia.vk_android_hw2_evseenkovia.ui.ImageUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ImageListViewModel(
    val repository: ImageRepositoryImpl
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

            val result = repository.loadImages(pageSize, 0)
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
        if (currentState !is ImageListUiState.Content || isLoading) return

        viewModelScope.launch {
            isLoading = true
            _state.value = currentState.copy(isPaginationLoading = true)

            val result = repository.loadImages(pageSize, currentPage * pageSize)
            isLoading = false

            result.onSuccess { newItems ->
                currentPage++
                _state.value = ImageListUiState.Content(
                    items = currentState.items + newItems,
                    isPaginationLoading = false
                )
            }.onFailure {
                // при ошибке на пагинации просто скрываем лоадер
                _state.value = currentState.copy(isPaginationLoading = false)
            }
        }
    }

    fun loadFromCache() {
        viewModelScope.launch {
            _state.value = ImageListUiState.Loading
            val cachedItems = repository.fetchAllFromLocal()
            if (cachedItems.isNotEmpty()) {
                _state.value = ImageListUiState.Content(
                    items = cachedItems,
                    isPaginationLoading = false
                )
            } else {
                _state.value = ImageListUiState.Error()
            }
        }
    }

    fun onImageClick(item: ImageUi) {
        // TODO: реализация уведомления/навигации
    }
}

