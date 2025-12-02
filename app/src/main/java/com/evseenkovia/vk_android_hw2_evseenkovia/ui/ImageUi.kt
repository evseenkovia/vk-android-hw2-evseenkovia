package com.evseenkovia.vk_android_hw2_evseenkovia.ui

import androidx.compose.ui.unit.Dp

data class ImageUi(
    val id: String,
    val url: String,
    val height: Dp,
    val uuid: String // уникальный ключ для LazyGrid
)
