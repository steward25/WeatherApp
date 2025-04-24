package com.stewardapostol.weatherapp.data.api

import androidx.room.*
import com.stewardapostol.weatherapp.data.model.CurrentWeatherEntity

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: CurrentWeatherEntity)

    @Query("SELECT * FROM current_weather WHERE city_id = :cityId LIMIT 1")
    suspend fun getWeatherByCityId(cityId: Int): CurrentWeatherEntity?

    @Query("SELECT * FROM current_weather LIMIT 1")
    suspend fun getAllCurrentWeather(): CurrentWeatherEntity?

    @Query("DELETE FROM current_weather")
    suspend fun clearAll()
}
