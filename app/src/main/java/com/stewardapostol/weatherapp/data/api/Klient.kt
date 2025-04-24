package com.stewardapostol.weatherapp.data.api

import android.util.Log
import com.stewardapostol.weatherapp.data.model.*
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * This object is where I setup the Ktor HTTP client.
 * I use this to call different APIs for weather and location stuff.
 */
object Klient {

    private const val TAG = "Klient"

    /**
     * This creates the client with needed plugins.
     * I set JSON stuff, headers, and response logging here.
     */
    fun client(): HttpClient {
        return HttpClient(Android) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    useAlternativeNames = true
                    ignoreUnknownKeys = true
                    encodeDefaults = false
                })
            }

            install(ResponseObserver) {
                onResponse { response ->
                    when (response.status.value) {
                        in 300..399 -> throw RedirectResponseException(response, "HTTP ${response.status.value}")
                        in 400..499 -> throw ClientRequestException(response, "HTTP ${response.status.value}")
                        in 500..599 -> throw ServerResponseException(response, "HTTP ${response.status.value}")
                        else -> if (response.status.value >= 600) {
                            throw ResponseException(response, "HTTP ${response.status.value}")
                        }
                    }
                    Log.e(this@Klient::class.simpleName, "Response: ${response.status.value}")
                }
            }

            install(DefaultRequest) {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }
        }
    }

    /**
     * Gets the current weather using OpenWeatherMap API.
     *
     * @param lat Latitude of the place
     * @param lon Longitude of the place
     * @param apiKey The API key from OpenWeather
     * @param errorCallBack To handle and return error message if something goes wrong
     *
     * @return Weather data or null if failed
     */
    suspend fun getWeatherData(
        lat: Double,
        lon: Double,
        apiKey: String,
        errorCallBack: (ErrorResponse) -> Unit
    ): CurrentWeatherEntity? {
        return try {
            val url = ApiEndpoints.currentWeather(lat, lon, apiKey)
            val response = client().get(url)
            Log.e(TAG, "Current Weather Response: ${response.status}, body=${response.bodyAsText()}")
            response.body()
        } catch (e: ClientRequestException) {
            errorCallBack(ErrorResponse(0, e.localizedMessage ?: "Client error"))
            null
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error fetching weather data", e)
            errorCallBack(ErrorResponse(0, e.localizedMessage ?: "Unknown error"))
            null
        }
    }

    /**
     * Gets 8-day forecast using OpenWeather OneCall API.
     *
     * @param lat Latitude
     * @param lon Longitude
     * @param apiKey API key from OpenWeather
     * @return Forecast data or null if error happened
     */
    suspend fun getEightDaysWeatherForecast(
        lat: Double,
        lon: Double,
        apiKey: String,
    ): ForecastWeatherEntity? {
        return try {
            val url = ApiEndpoints.forecastWeather(lat, lon, apiKey)
            val response = client().get(url)
            when (response.status) {
                HttpStatusCode.OK -> response.body()
                HttpStatusCode.Unauthorized -> {
                    Log.e(TAG, "Unauthorized access")
                    null
                }
                else -> {
                    Log.e(TAG, "Unexpected status: ${response.status}")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching 8-day forecast", e)
            null
        }
    }

    /**
     * Gets the latitude and longitude based on user's public IP.
     *
     * @param errorCallback To send error back if anything fails
     * @return Pair of lat/lon or null
     */
    suspend fun getLatLongFromPublicIP(
        errorCallback: (ErrorResponse) -> Unit
    ): Pair<Double, Double>? {
        return try {
            val geoResponse = client().get(ApiEndpoints.GEOLOCATION_FROM_IP).body<GeoLocationResponse>()
            errorCallback(ErrorResponse(0, "geo response: $geoResponse"))

            if (geoResponse.success && geoResponse.latitude != null && geoResponse.longitude != null) {
                geoResponse.latitude to geoResponse.longitude
            } else {
                errorCallback(ErrorResponse(0, geoResponse.message ?: "Failed to retrieve geolocation"))
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get lat/lon from IP", e)
            errorCallback(ErrorResponse(0, e.localizedMessage ?: "Unknown error"))
            null
        }
    }


}
