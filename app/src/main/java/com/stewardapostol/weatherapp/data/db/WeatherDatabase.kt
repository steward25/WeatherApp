package com.stewardapostol.weatherapp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.stewardapostol.weatherapp.data.model.CurrentWeatherEntity
import com.stewardapostol.weatherapp.data.api.WeatherDao
import com.stewardapostol.weatherapp.data.api.WeatherForecastDao
import com.stewardapostol.weatherapp.data.local.Converters
import com.stewardapostol.weatherapp.data.model.ForecastWeatherEntity

@Database(
    entities = [CurrentWeatherEntity::class, ForecastWeatherEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class WeatherDatabase : RoomDatabase() {

    companion object {
        @Volatile
        private var INSTANCE: WeatherDatabase? = null

        fun db(context: Context): WeatherDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WeatherDatabase::class.java,
                    "weather_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }

    abstract fun currentWeatherDao(): WeatherDao
    abstract fun weatherForecastDao(): WeatherForecastDao
}