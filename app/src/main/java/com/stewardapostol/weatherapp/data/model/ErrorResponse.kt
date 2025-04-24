package com.stewardapostol.weatherapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val cod: Int,
    val message: String
): AppsData()