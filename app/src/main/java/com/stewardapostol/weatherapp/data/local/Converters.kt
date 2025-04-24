package com.stewardapostol.weatherapp.data.local

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json
import com.stewardapostol.weatherapp.data.model.DailyForecast
import com.stewardapostol.weatherapp.data.model.Weather

class Converters {

    private val json = Json { isLenient = true; ignoreUnknownKeys = true }

    @TypeConverter
    fun fromWeatherList(weatherList: List<Weather>?): String? {
        return weatherList?.let { json.encodeToString(it) }
    }

    @TypeConverter
    fun toWeatherList(weatherListJson: String?): List<Weather>? {
        return weatherListJson?.let { json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromDailyList(value: List<DailyForecast>): String = json.encodeToString(value)

    @TypeConverter
    fun toDailyList(value: String): List<DailyForecast> = json.decodeFromString(value)
}