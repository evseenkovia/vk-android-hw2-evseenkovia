package com.evseenkovia.vk_android_hw2_evseenkovia.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "images")
data class ImageEntity(
    @PrimaryKey val uuid: String, // уникальный id для Room
    val id: String,               // оригинальный id с Giphy
    val url: String
)