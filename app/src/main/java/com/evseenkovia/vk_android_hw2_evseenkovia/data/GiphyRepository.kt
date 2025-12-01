package com.evseenkovia.vk_android_hw2_evseenkovia.data

import com.evseenkovia.vk_android_hw2_evseenkovia.domain.ImageRepository
import com.evseenkovia.vk_android_hw2_evseenkovia.ui.ImageUi

class GiphyRepository(
    private val api: GiphyApiService = RetrofitInstance.api,
    private val apiKey: String
) : ImageRepository {

    override suspend fun loadPage(page: Int, pageSize: Int): Result<List<ImageUi>> {
        return try {
            val offset = page * pageSize
            val response = api.getTrendingGifs(apiKey, pageSize, offset)
            val items = response.data.mapIndexed { index, gif ->
                ImageUi(
                    id = gif.id,
                    url = gif.images.original.url,
                    index = offset + index
                )
            }
            Result.success(items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
