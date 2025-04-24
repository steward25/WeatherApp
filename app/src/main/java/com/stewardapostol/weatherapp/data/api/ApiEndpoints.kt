package com.stewardapostol.weatherapp.data.api

object ApiEndpoints {
    const val BASE_URL_WEATHER = "https://api.openweathermap.org/data/"
    const val GEOLOCATION_FROM_IP = "https://ipwho.is/"


    fun currentWeather(lat: Double, lon: Double, apiKey: String): String =
        "${BASE_URL_WEATHER}2.5/weather?lat=$lat&lon=$lon&appid=$apiKey&units=metric"

    fun forecastWeather(lat: Double, lon: Double, apiKey: String): String =
        "${BASE_URL_WEATHER}3.0/onecall?lat=$lat&lon=$lon&exclude=minutely,hourly,alerts,current&appid=$apiKey&units=metric"

    fun weatherIcon(iconCode: String?): String? =
        iconCode?.let { "https://openweathermap.org/img/wn/${it}@2x.png" }

}
