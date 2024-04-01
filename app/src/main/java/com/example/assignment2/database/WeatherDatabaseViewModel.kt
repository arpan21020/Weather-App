package com.example.assignment2.database

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment2.getCurrentDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WeatherDatabaseViewModel(application: Application): AndroidViewModel(application) {

    val allData: LiveData<List<Weather>>
    private val repository: WeatherDBrepository

    init{
        val wordDao= WeatherDatabase.getDatabase(application).getDao()
        repository= WeatherDBrepository(wordDao)
        allData=repository.allData.asLiveData()
//        fetchMinTemp(getCurrentDate())
    }
    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun upsert(weather: Weather) = viewModelScope.launch(Dispatchers.IO) {
        repository.upsert(weather)
    }

    fun delete(weather: Weather)=viewModelScope.launch(Dispatchers.IO) {
        repository.delete(weather)
    }

    fun clearAllData()=viewModelScope.launch(Dispatchers.IO) {
        repository.clearAllData()
    }



}