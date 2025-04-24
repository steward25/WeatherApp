package com.stewardapostol.weatherapp.view

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.stewardapostol.weatherapp.R

object WeatherIcons {

    fun getWeatherIcons(context : Context, icon : String ) : Drawable? {
        when (icon) {
            "01d" -> return ContextCompat.getDrawable(context,R.drawable.w01d)
            "01n" -> return ContextCompat.getDrawable(context,R.drawable.w01n)
            "02d" -> return ContextCompat.getDrawable(context,R.drawable.w02d)
            "02n" -> return ContextCompat.getDrawable(context,R.drawable.w02n)
            "03d" -> return ContextCompat.getDrawable(context,R.drawable.w03d)
            "04d" -> return ContextCompat.getDrawable(context,R.drawable.w04d)
            "04n" -> return ContextCompat.getDrawable(context,R.drawable.w04n)
            "09d" -> return ContextCompat.getDrawable(context,R.drawable.w09d)
            "09n" -> return ContextCompat.getDrawable(context,R.drawable.w09n)
            "10d" -> return ContextCompat.getDrawable(context,R.drawable.w10d)
            "10n" -> return ContextCompat.getDrawable(context,R.drawable.w10n)
            "11d" -> return ContextCompat.getDrawable(context,R.drawable.w11d)
            "11n" -> return ContextCompat.getDrawable(context,R.drawable.w11n)
            "13d" -> return ContextCompat.getDrawable(context,R.drawable.w13d)
            "13n" -> return ContextCompat.getDrawable(context,R.drawable.w13n)
            "50d" -> return ContextCompat.getDrawable(context,R.drawable.w50d)
            "50n" -> return ContextCompat.getDrawable(context,R.drawable.w50n)
        }
        return null
    }
}