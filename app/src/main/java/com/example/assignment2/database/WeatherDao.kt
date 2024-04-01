package com.example.assignment2.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    @Upsert
    suspend fun upsertData(weather:Weather)

    @Delete
    suspend fun deleteData(weather: Weather)

    @Query("SELECT * from weather_details")
    fun getAllData(): Flow<List<Weather>>

    @Query("DELETE FROM weather_details")
    suspend fun clearAllData()




}