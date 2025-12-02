package com.evseenkovia.vk_android_hw2_evseenkovia.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ImageDao {

    @Query("SELECT DISTINCT * FROM images")
    suspend fun getAll(): List<ImageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ImageEntity>)

    @Query("DELETE FROM images")
    suspend fun clear()
}