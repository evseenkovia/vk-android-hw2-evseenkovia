package com.evseenkovia.vk_android_hw2_evseenkovia.ui

sealed interface ImageListUiState {
    data object Loading : ImageListUiState
    data class Content(
        val items: List<ImageUi>,
        val isPaginationLoading: Boolean
    ) : ImageListUiState

    data class Error(
        val showRetry: Boolean = true
    ) : ImageListUiState
}