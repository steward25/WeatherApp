package com.stewardapostol.weatherapp.view

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.stewardapostol.weatherapp.R
import com.stewardapostol.weatherapp.data.model.CurrentWeatherEntity
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object WeatherBindingAdapters {

    @JvmStatic
    @BindingAdapter("weatherEntity")
    fun View.weatherView(weatherEntity: CurrentWeatherEntity?) {
        weatherEntity?.let { weather ->
            when (this) {
                is ImageView -> {
                    when (id) {
                        R.id.ivWeatherIcon -> loadWeatherIcon(weather.weather?.firstOrNull()?.icon, context)
                        else -> {}
                    }
                }
                is TextView -> {
                    when (id) {
                        R.id.tvLocation -> {
                            val countryName = Locale("", weather.sys?.country ?: "").displayCountry
                            text = "${weather.cityName}, $countryName"
                        }
                        R.id.tvTemperature -> text = weather.main?.let {
                            String.format(
                                context.getString(R.string.temperature_unit),
                                it.tempMax
                            )
                        }
                        R.id.tvSunrise -> convertTimestampToTime(weather.sys?.sunrise, weather.timezone)
                        R.id.tvSunset -> convertTimestampToTime(weather.sys?.sunset, weather.timezone)
                        else -> {}
                    }
                }
                else -> {}
            }
        }
    }
}

fun ImageView.loadWeatherIcon(iconCode: String?, context: Context) {
    iconCode?.let { icon ->
        try {
            load(WeatherIcons.getWeatherIcons(context, icon)) {
                crossfade(true)
                error(R.drawable.ic_weather_placeholder)
            }
        } catch (e: Exception) {
            Log.e("ImageView", "Error loading weather icon: ${e.message}")
        }
    } ?: run {
        load(R.drawable.ic_weather_placeholder)
    }
}

fun TextView.convertTimestampToTime(timestamp: Long?, timezoneOffsetSeconds: Int?) {
    if (timestamp != null && timezoneOffsetSeconds != null) {
        val timezoneOffsetMillis = TimeUnit.SECONDS.toMillis(timezoneOffsetSeconds.toLong())
        val date = Date((timestamp + timezoneOffsetMillis) * 1000)
        val sdf = SimpleDateFormat("h:mm", Locale.getDefault())
        val formattedTime = sdf.format(date).uppercase(Locale.getDefault())
        text = String.format("%s",formattedTime).trim()
    } else {
        text = "--:--"
    }
}