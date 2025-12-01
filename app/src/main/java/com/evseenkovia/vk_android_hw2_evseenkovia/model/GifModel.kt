package com.evseenkovia.vk_android_hw2_evseenkovia.model

data class GiphyResponse(
    val data: List<GifObject>
)

data class GifObject(
    val id: String,
    val images: GifImages
)

data class GifImages(
    val original: GifOriginal
)

data class GifOriginal(
    val url: String
)
