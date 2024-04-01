package com.example.assignment2.api

import com.example.assignment2.data.ForecastData
import com.example.assignment2.data.WeatherData
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class WeatherApiService() {
    private lateinit var api: Weatherapi
    private lateinit var api2: Weatherapi2
    init{
        val retrofit = Retrofit.Builder()
            .baseUrl("https://archive-api.open-meteo.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        api=retrofit.create(Weatherapi::class.java)
        val retrofit2=Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/v1/")
            .addConverterFactory((GsonConverterFactory.create()))
            .build()
        api2=retrofit2.create(Weatherapi2::class.java)
    }

    suspend fun getWeather(latitude: Double?, longitude: Double?, currentDate: String):WeatherData{
        return api.getWeather(latitude,longitude, endDate = currentDate)
    }

    suspend fun getForecast(latitude: Double?,longitude: Double?):ForecastData{
        return api2.getforecast(latitude,longitude)
    }
    interface Weatherapi{
            @GET("archive")
            suspend fun getWeather(
                @Query("latitude") latitude: Double?=37.4219983,
                @Query("longitude") longitude: Double?=-122.084,
                @Query("start_date") startDate: String = "2014-01-01",
                @Query("end_date") endDate: String = "2024-03-02",
                @Query("hourly") hourly: String = "temperature_2m",
                @Query("daily") daily: List<String> = listOf("temperature_2m_max", "temperature_2m_min"),
                @Query("timezone") timezone: String = "auto"
            ): WeatherData


//        suspend fun getWeather():WeatherData
    }
    interface Weatherapi2{
        @GET("forecast")
        suspend fun getforecast(
            @Query("latitude") latitude: Double?,
            @Query("longitude") longitude: Double?,
            @Query("daily") daily: List<String> =listOf("temperature_2m_max", "temperature_2m_min"),
            @Query("timezone") timezone: String="auto"
            ): ForecastData


    }
}