package com.stewardapostol.weatherapp.data.model

import androidx.room.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import com.stewardapostol.weatherapp.data.local.Converters

@Entity(tableName = "forecast_weather")
@Serializable
data class ForecastWeatherEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "city_lat")
    val lat: Double? = null,

    @ColumnInfo(name = "city_lon")
    val lon: Double? = null,

    @ColumnInfo(name = "timezone")
    val timezone: String? = null,

    @ColumnInfo(name = "timezone_offset")
    val timezoneOffset: Int? = null,

    @TypeConverters(Converters::class)
    @ColumnInfo(name = "daily")
    val daily: List<DailyForecast>? = null
) : AppsData()

@Serializable
data class DailyForecast(
    @SerialName("dt")
    val dateTime: Long? = null,

    @SerialName("sunrise")
    val sunrise: Long? = null,

    @SerialName("sunset")
    val sunset: Long? = null,

    @SerialName("temp")
    val temp: Temp? = null,

    @SerialName("feels_like")
    val feelsLike: FeelsLike? = null,

    @SerialName("pressure")
    val pressure: Int? = null,

    @SerialName("humidity")
    val humidity: Int? = null,

    @SerialName("weather")
    val weather: List<Weather>? = null,

    @SerialName("wind_speed")
    val windSpeed: Double? = null,

    @SerialName("wind_deg")
    val windDeg: Int? = null,

    @SerialName("clouds")
    val clouds: Int? = null,

    @SerialName("pop")
    val pop: Double? = null,

    @SerialName("uvi")
    val uvi: Double? = null
)

@Serializable
data class Temp(
    val day: Double? = null,
    val min: Double? = null,
    val max: Double? = null,
    val night: Double? = null,
    val eve: Double? = null,
    val morn: Double? = null
)

@Serializable
data class FeelsLike(
    val day: Double? = null,
    val night: Double? = null,
    val eve: Double? = null,
    val morn: Double? = null
)
