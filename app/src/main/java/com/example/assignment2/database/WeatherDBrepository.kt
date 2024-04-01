package com.example.assignment2.database

import com.example.assignment2.data.WeatherData
import kotlinx.coroutines.flow.Flow

class WeatherDBrepository(private val dao:WeatherDao) {
    val allData: Flow<List<Weather>> =dao.getAllData()


    suspend fun upsert(weather: Weather){
        dao.upsertData(weather)
    }

    suspend fun delete(weather: Weather){
        dao.deleteData(weather)
    }
    suspend fun clearAllData(){
        dao.clearAllData()
    }


}