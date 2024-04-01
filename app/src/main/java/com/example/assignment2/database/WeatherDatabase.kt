package com.example.assignment2.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase



val DATABASE_NAME: String= "weather_database"

@Database(
    entities = [Weather::class],
    version = 1
)
abstract class WeatherDatabase:RoomDatabase() {
    abstract fun getDao():WeatherDao

    companion object{
        @Volatile
        private var INSTANCE: WeatherDatabase?=null

        fun getDatabase(context: Context):WeatherDatabase{
            return INSTANCE?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WeatherDatabase::class.java,
                    DATABASE_NAME
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }

}