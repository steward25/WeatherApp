package com.stewardapostol.weatherapp.data.db

import com.stewardapostol.weatherapp.data.model.AppsData
import com.stewardapostol.weatherapp.data.model.CurrentWeatherEntity
import com.stewardapostol.weatherapp.data.model.ForecastWeatherEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * This function returns a Flow from Room DB based on the type of data passed.
 * So like if you ask for current weather, it goes to currentWeatherDao,
 * and if it's forecast, it goes to forecastWeatherDao.
 *
 * @param db The Room database instance
 * @return Flow of the requested data type or null if not matched
 */
inline fun <reified DATA : AppsData> getDAO(db: WeatherDatabase): Flow<DATA?> = flow {
    when (DATA::class) {
        CurrentWeatherEntity::class -> {
            val weather = db.currentWeatherDao().getAllCurrentWeather()
            emit(weather as DATA?)
        }
        ForecastWeatherEntity::class -> {
            val weather = db.weatherForecastDao().getAllForecastWeather()
            emit(weather as DATA?)
        }
        else -> {
            emit(null)
        }
    }
}

/**
 * This is used to insert data into the correct table depending on the type.
 * So if you pass current weather, it stores that. If forecast, it stores forecast.
 *
 * @param db The Room DB instance
 * @param data The data you want to save
 */
suspend inline fun <reified DATA : AppsData> insertDao(db: WeatherDatabase, data: AppsData) {
    when (DATA::class) {
        CurrentWeatherEntity::class -> db.currentWeatherDao().insertWeather(data as CurrentWeatherEntity)
        ForecastWeatherEntity::class -> db.weatherForecastDao().insertForecastWeather(data as ForecastWeatherEntity)
        else -> error("Unsupported data type: ${DATA::class}")
    }
}

/**
 * This deletes all the entries from the table based on the type you give.
 * Clears current or forecast weather depending on what you need.
 *
 * @param db The Room DB instance
 */
suspend inline fun <reified DATA : AppsData> deleteDao(db: WeatherDatabase) {
    when (DATA::class) {
        CurrentWeatherEntity::class -> db.currentWeatherDao().clearAll()
        ForecastWeatherEntity::class -> db.weatherForecastDao().clearAllForecastWeather()
        else -> error("Unsupported data type: ${DATA::class}")
    }
}
