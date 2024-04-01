package com.example.assignment2.api

import com.example.assignment2.data.ForecastData
import com.example.assignment2.data.WeatherData

class WeatherApiRepository() {
    private val weatherService: WeatherApiService= WeatherApiService()

    suspend fun getWeather(latitude: Double?, longitude: Double?, currentDate: String):WeatherData{
        return weatherService.getWeather(latitude,longitude,currentDate)
    }
    suspend fun getForecast(latitude: Double?, longitude: Double?):ForecastData{
        return weatherService.getForecast(latitude,longitude)
    }
}