package com.example.assignment2.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_details")
data class Weather(
    @PrimaryKey(autoGenerate = true)
    val id:Int?=null,
    val date:String,
    val minTemp:Double?,
    val maxTemp:Double?
)