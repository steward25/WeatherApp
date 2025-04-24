package com.stewardapostol.weatherapp.data.model

import androidx.room.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.stewardapostol.weatherapp.data.local.Converters

@Entity(tableName = "current_weather")
@Serializable
data class CurrentWeatherEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "city_id")
    @SerialName("id")
    val cityId: Int? = null, 
    @ColumnInfo(name = "city_name")
    @SerialName("name")
    val cityName: String? = null, 
    @Embedded
    @SerialName("coord")
    val coord: Coord? = null, 
    @TypeConverters(Converters::class)
    @ColumnInfo(name = "weather_main")
    @SerialName("weather")
    val weather: List<Weather>? = null, 
    @Embedded(prefix = "main_")
    @SerialName("main")
    val main: Main? = null, 
    @ColumnInfo(name = "visibility")
    @SerialName("visibility")
    val visibility: Int? = null, 
    @Embedded
    @SerialName("wind")
    val wind: Wind? = null, 
    @Embedded
    @SerialName("clouds")
    val clouds: Clouds? = null, 
    @ColumnInfo(name = "timestamp")
    @SerialName("dt")
    val timestamp: Long? = null, 
    @Embedded
    @SerialName("sys")
    val sys: Sys? = null, 
    @ColumnInfo(name = "timezone")
    @SerialName("timezone")
    val timezone: Int? = null 
) : AppsData()

@Serializable
data class Coord(
    @SerialName("lon")
    val lon: Double? = null, 
    @SerialName("lat")
    val lat: Double? = null 
)

@Serializable
data class Weather(
    @SerialName("id")
    val id: Int? = null, 
    @SerialName("main")
    val main: String? = null, 
    @SerialName("description")
    val description: String? = null, 
    @SerialName("icon")
    val icon: String? = null 
)

@Serializable
data class Main(
    @SerialName("temp")
    val temp: Double? = null, 
    @SerialName("feels_like")
    val feelsLike: Double? = null, 
    @SerialName("temp_min")
    val tempMin: Double? = null, 
    @SerialName("temp_max")
    val tempMax: Double? = null, 
    @SerialName("pressure")
    val pressure: Int? = null, 
    @SerialName("humidity")
    val humidity: Int? = null, 
    @SerialName("sea_level")
    val seaLevel: Int? = null, 
    @SerialName("grnd_level")
    val groundLevel: Int? = null 
)

@Serializable
data class Wind(
    @SerialName("speed")
    val speed: Double? = null, 
    @SerialName("deg")
    val deg: Int? = null 
)

@Serializable
data class Clouds(
    @SerialName("all")
    val cloudiness: Int? = null 
)

@Serializable
data class Sys(
    @SerialName("type")
    val type: Int? = null, 
    @SerialName("id")
    val id: Int? = null, 
    @SerialName("country")
    val country: String? = null, 
    @SerialName("sunrise")
    val sunrise: Long? = null, 
    @SerialName("sunset")
    val sunset: Long? = null 
)

@Serializable
open class AppsData
