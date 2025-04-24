package com.stewardapostol.weatherapp.data.api

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.stewardapostol.weatherapp.data.model.ForecastWeatherEntity

@Dao
interface WeatherForecastDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecastWeather(forecastWeather: ForecastWeatherEntity)


    @Query("SELECT * FROM forecast_weather WHERE id = :id LIMIT 1")
    suspend fun getForecastWeatherById(id: Int): ForecastWeatherEntity?


    @Query("SELECT * FROM forecast_weather LIMIT 1")
    suspend fun getAllForecastWeather(): ForecastWeatherEntity?


    @Query("DELETE FROM forecast_weather")
    suspend fun clearAllForecastWeather()


    @Query("SELECT * FROM forecast_weather WHERE city_lat = :lat AND city_lon = :lon LIMIT 1")
    suspend fun getDailyForecastByLocation(lat: Double, lon: Double): ForecastWeatherEntity?

}
