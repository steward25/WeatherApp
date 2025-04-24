package com.stewardapostol.weatherapp.data.repository

import android.app.Application
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.room.withTransaction
import com.stewardapostol.weatherapp.data.api.Klient
import com.stewardapostol.weatherapp.data.model.CurrentWeatherEntity
import com.stewardapostol.weatherapp.data.db.WeatherDatabase
import com.stewardapostol.weatherapp.data.db.deleteDao
import com.stewardapostol.weatherapp.data.db.getDAO
import com.stewardapostol.weatherapp.data.db.insertDao
import com.stewardapostol.weatherapp.data.model.AppsData
import com.stewardapostol.weatherapp.data.model.ErrorResponse
import com.stewardapostol.weatherapp.data.model.ForecastWeatherEntity
import com.stewardapostol.weatherapp.util.LocationHelper
import com.stewardapostol.weatherapp.util.Resource
import com.stewardapostol.weatherapp.util.networkBoundResource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.Json

/**
 * This object handles all the data stuff for the app.
 * It deals with the database and the API, and decides what to use when.
 */
object Repository {

    /**
     * Gets user's location, then fetches current and forecast weather using that.
     * It calls your callback with the result when ready.
     *
     * @param app Your app context
     * @param apiKey API key for OpenWeather
     * @param callback Callback for returning weather data
     * @param locBack Callback for returning location object
     */
    fun fetchWeatherBasedOnLocation(
        app: Application,
        apiKey: String,
        callback: (AppsData?) -> Unit,
        locBack: (Location?) -> Unit
    ) {
        // Tries to get the best location available
        LocationHelper(app).getBestAvailableLocation { location ->
            location?.let {
                val lat = it.latitude
                val lon = it.longitude

                locBack.invoke(location)

                CoroutineScope(Dispatchers.IO).launch {
                    // Get current weather
                    val weatherFlow = async {
                        getData<CurrentWeatherEntity>(
                            app = app,
                            shouldFetch = true,
                            lat = lat,
                            lon = lon,
                            apiKey = apiKey
                        )
                    }

                    // Get 8-day forecast
                    val weatherForecastFlow = async {
                        getEightDayForecastData(
                            app = app,
                            shouldFetch = true,
                            lat = lat,
                            lon = lon,
                            apiKey = apiKey
                        )
                    }

                    // Load backup forecast from assets just in case
                    val forecastsFromAssets = async {
                        loadForecastFromAssetsFlow(app)
                    }

                    // Handle current weather result
                    weatherFlow.await().collect { resource ->
                        callback(resource.data)
                    }

                    // Handle forecast result
                    weatherForecastFlow.await().collectLatest { resource ->
                        callback(resource.data)
                    }

                    // Optional debug log for asset fallback
                    forecastsFromAssets.await().collectLatest {
                        Log.e("APOSTOL", "forecastsFromAssets: $it")
                    }
                }
            } ?: run {
                callback(null) // If location not found
            }
        }
    }

    /**
     * Generic function to fetch any type of weather data from API and DB.
     * Caches it in the DB after fetching from API if needed.
     *
     * @param app App context
     * @param shouldFetch If true, fetches from API
     * @param lat Latitude
     * @param lon Longitude
     * @param apiKey API key
     */
    inline fun <reified Data : AppsData> getData(
        app: Application,
        shouldFetch: Boolean = false,
        lat: Double,
        lon: Double,
        apiKey: String
    ): Flow<Resource<Data?>> {

        val db = WeatherDatabase.db(app)

        return flow {
            var errorResponse: ErrorResponse? = null

            val resource = networkBoundResource(
                shouldFetch = { shouldFetch },
                query = { getDAO<Data>(db) },
                fetch = {
                    Klient.getWeatherData(lat, lon, apiKey) { error ->
                        errorResponse = error
                    }
                },
                saveFetchResult = { data ->
                    data?.let {
                        db.withTransaction {
                            deleteDao<Data>(db)
                            insertDao<Data>(db, it)
                        }
                    }
                }
            )

            // Just in case there was an error, wrap it up
            resource.collect {
                if (errorResponse != null) {
                    emit(Resource.Error(null, it.data))
                } else {
                    emit(it)
                }
            }
        }
    }

    /**
     * This gets the 8-day forecast either from API or DB.
     * If there's an error, it'll try loading from local assets as a backup.
     *
     * @param app App context
     * @param shouldFetch Whether to fetch from API
     * @param lat Latitude
     * @param lon Longitude
     * @param apiKey Your API key
     */
    fun getEightDayForecastData(
        app: Application,
        shouldFetch: Boolean = false,
        lat: Double,
        lon: Double,
        apiKey: String,
    ): Flow<Resource<ForecastWeatherEntity?>> {
        val db = WeatherDatabase.db(app)

        return networkBoundResource(
            shouldFetch = { shouldFetch },
            query = { getDAO<ForecastWeatherEntity>(db) },
            fetch = {
                var result = Klient.getEightDaysWeatherForecast(lat, lon, apiKey)

                //Use this as Fallback, if you are not authorize to use open weather api, pay first
                if (result is ErrorResponse) {
                    loadForecastFromAssetsFlow(app).collect { forecast ->
                        result = forecast
                    }
                }

                Log.i("APOSTOL", "getEightDayForecastData: $result")
                result
            },
            saveFetchResult = { data ->
                data?.let {
                    db.withTransaction {
                        deleteDao<ForecastWeatherEntity>(db)
                        insertDao<ForecastWeatherEntity>(db, it)
                    }
                }
            }
        )
    }

    /**
     * Loads a local fallback JSON forecast file from the assets folder.
     * This is used when API call fails or no internet.
     *
     * @param context Context to access assets
     * @param fileName File name (optional, default is sample)
     */
    fun loadForecastFromAssetsFlow(
        context: Context,
        fileName: String = "eigth_day_forecast_sample_from_open_weather.json"
    ): Flow<ForecastWeatherEntity> = flow {
        val jsonString = withContext(Dispatchers.IO) {
            context.assets.open(fileName).bufferedReader().use { it.readText() }
        }
        val forecast = Json { ignoreUnknownKeys = true }
            .decodeFromString<ForecastWeatherEntity>(jsonString)
        emit(forecast)
    }
}
