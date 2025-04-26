package com.stewardapostol.weatherapp.viewmodel

import android.app.Application
import android.location.Location
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.stewardapostol.weatherapp.data.model.AppsData
import com.stewardapostol.weatherapp.data.model.CurrentWeatherEntity
import com.stewardapostol.weatherapp.data.model.ErrorResponse
import com.stewardapostol.weatherapp.data.model.ForecastWeatherEntity
import com.stewardapostol.weatherapp.data.repository.Repository
import com.stewardapostol.weatherapp.data.repository.Repository.loadForecastFromAssetsFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = WeatherViewModel::class.simpleName

    private val _weatherData = MutableStateFlow<AppsData?>(null)
    val weatherData: StateFlow<AppsData?> = _weatherData

    private val _weatherForecastData = MutableStateFlow<AppsData?>(null)
    val weatherForecastData: StateFlow<AppsData?> = _weatherForecastData

    private val _locMessage = MutableStateFlow<Location?>(null)
    val locMessage: StateFlow<Location?> = _locMessage

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun fetchAllWeatherData(apiKey: String) {
        _isLoading.value = true
        Repository.fetchWeatherBasedOnLocation(
            getApplication(), apiKey,
            callback = { data ->
                _isLoading.value = false

                if (data != null) {
                    when (data) {
                        is CurrentWeatherEntity -> _weatherData.value = data
                        is ForecastWeatherEntity -> _weatherForecastData.value = data
                        is ErrorResponse -> _errorMessage.value = data.message
                    }
                    Log.e(TAG, "@fetchAllWeatherData: $data")
                } else {
                    _errorMessage.value = "Failed to fetch data."
                    Log.e(TAG, "@fetchAllWeatherData: Failed to fetch weather data.")
                }
            },
            locBack = {
                _locMessage.value = it
            }
        )
    }

    fun fetchForecastFromAssets() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                loadForecastFromAssetsFlow(getApplication()).collect { forecast ->
                    _weatherForecastData.value = forecast
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load from assets: ${e.localizedMessage}"
                Log.e(TAG, "fetchForecastFromAssets error: ", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}

