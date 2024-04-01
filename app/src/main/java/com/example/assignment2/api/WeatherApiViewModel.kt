package com.example.assignment2.api

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment2.data.Daily
import com.example.assignment2.data.DailyUnits
import com.example.assignment2.data.ForecastData
import com.example.assignment2.data.WeatherData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter


fun getCurrentDate(): String {
    val currentDate = LocalDate.now().minusDays(1)
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return currentDate.format(formatter)
}
class WeatherApiViewModel : ViewModel() {
    val latitude = MutableLiveData<Double>(0.0)
    val longitude = MutableLiveData<Double>(0.0)
    val currentDate = getCurrentDate()

    private val repository: WeatherApiRepository = WeatherApiRepository()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            updateWeatherData()
        }
    }

    fun updateLocation(latitude: Double, longitude: Double) {
        this.latitude.value = latitude
        this.longitude.value = longitude
        println("latitude: $latitude : longitude: $longitude")
        viewModelScope.launch(Dispatchers.IO) {
            updateWeatherData()
        }
    }

    val weatherState: MutableState<WeatherData> = mutableStateOf(
        WeatherData(
            latitude = 0.0,
            longitude = 0.0,
            generationTimeMs = 0.0,
            utcOffsetSeconds = 0,
            timezone = "",
            timezoneAbbreviation = "",
            elevation = 0,
            dailyUnits = DailyUnits("", "", ""),
            daily = Daily(emptyList(), emptyList(), emptyList())
        )
    )

    val forecastState: MutableState<ForecastData> = mutableStateOf(
        ForecastData(
            latitude = 0.0,
            longitude = 0.0,
            timezone = "",
            daily = Daily(emptyList(), emptyList(), emptyList())
        )
    )

    private suspend fun updateWeatherData() {
        try{
            val weatherData = getWeather(latitude.value, longitude.value, currentDate)
            weatherState.value = weatherData

            val forecastData = getForecast(latitude.value, longitude.value)
            forecastState.value = forecastData

        }
        catch (e: Exception) {
            // Handle the exception appropriately
            // You can log the error, show a toast message, or handle it in any way suitable for your app
            Log.e("WeatherUpdate", "Error updating weather data: ${e.message}")
            // You might also want to notify the user about the error
            // For example, if you're using a ViewModel in an Android app, you can use LiveData to update the UI
            // errorState.value = "Failed to update weather data: ${e.message}"
        }
    }

    private suspend fun getWeather(latitude: Double?, longitude: Double?, currentDate: String): WeatherData {
        return repository.getWeather(latitude, longitude, currentDate)
    }

    private suspend fun getForecast(latitude: Double?, longitude: Double?): ForecastData {
        return repository.getForecast(latitude, longitude)
    }
}
