package com.evseenkovia.vk_android_hw2_evseenkovia.domain

import com.evseenkovia.vk_android_hw2_evseenkovia.ui.ImageUi

interface ImageRepository {
    suspend fun loadImages(limit: Int = 20, offset: Int = 0): Result<List<ImageUi>>
}