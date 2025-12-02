package com.evseenkovia.vk_android_hw2_evseenkovia.data.repository

import android.util.Log
import androidx.compose.ui.unit.dp
import com.evseenkovia.vk_android_hw2_evseenkovia.data.GiphyApiService
import com.evseenkovia.vk_android_hw2_evseenkovia.data.RetrofitInstance
import com.evseenkovia.vk_android_hw2_evseenkovia.data.local.ImageDao
import com.evseenkovia.vk_android_hw2_evseenkovia.data.local.ImageEntity
import com.evseenkovia.vk_android_hw2_evseenkovia.domain.ImageRepository
import com.evseenkovia.vk_android_hw2_evseenkovia.ui.ImageUi
import java.util.UUID

class ImageRepositoryImpl(
    private val dao: ImageDao,
    private val api: GiphyApiService = RetrofitInstance.api,
    private val apiKey: String
) : ImageRepository {

    override suspend fun loadImages(
        limit: Int,
        offset: Int
    ): Result<List<ImageUi>> {
        return try {
            // Получаем данные с API
            val response = api.getTrendingGifs(apiKey, limit, offset)

            // Преобразуем в ImageEntity
            val entities = response.data.map { gif ->
                val uuid = UUID.nameUUIDFromBytes(gif.id.toByteArray()).toString()
                ImageEntity(
                    uuid = uuid,
                    id = gif.id,
                    url = gif.images.original.url
                )
            }

            // Сохраняем в Room
            dao.insertAll(entities)
            entities.forEach { Log.d("GIF", "Saved: ${it.id}") } // <- тег "GIF"

            // Преобразуем в UI модель
            val uiItems = entities.map { it.toUi() }

            Result.success(uiItems)

        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("GIF", "Network error, fallback to Room", e) // <- тег "GIF"

            // Фоллбек на Room
            val cached = dao.getAll()
            if (cached.isEmpty()) return Result.failure(e)

            val uiItems = cached.map { it.toUi() }
            Result.success(uiItems)
        }
    }

    suspend fun fetchAllFromLocal(): List<ImageUi> {
        val cached = dao.getAll().map { it.toUi() }
        Log.d("GIF", "Fetched from local: ${cached.size}") // <- тег "GIF"
        return cached
    }

    // Конвертация ImageEntity в ImageUi
    private fun ImageEntity.toUi(): ImageUi {
        val randomHeight = (150..300).random().dp
        return ImageUi(
            id = id,
            url = url,
            height = randomHeight,
            uuid = uuid
        )
    }

    // Очистка кэша
    suspend fun clearCache() {
        dao.clear()
        Log.d("GIF", "Cache cleared") // <- тег "GIF"
    }
}
