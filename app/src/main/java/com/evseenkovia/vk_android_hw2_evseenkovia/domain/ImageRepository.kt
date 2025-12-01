package com.evseenkovia.vk_android_hw2_evseenkovia.domain

import com.evseenkovia.vk_android_hw2_evseenkovia.ui.ImageUi

interface ImageRepository {
    suspend fun loadPage(page: Int, pageSize: Int): Result<List<ImageUi>>
}