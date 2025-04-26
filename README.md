
# 🌦️ WeatherApp

A simple weather forecast app built with Kotlin, using the OpenWeatherMap API. It shows the current and future weather for your location, with a clean and modular architecture.

## 🔑 Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/your-username/weatherapp.git
cd weatherapp
```

### 2. Add your API key

You will need an API key from OpenWeatherMap.

Open the gradle.properties file in the root of your project.
Add or modify the following line, replacing YOUR_ACTUAL_OPENWEATHERMAP_API_KEY with your actual key:

```properties
API_KEY=YOUR_ACTUAL_OPENWEATHERMAP_API_KEY
```
## 🚀 Features

- Current weather based on geolocation
- 7-day weather forecast
- Clean architecture using MVVM
- Offline caching with Room
- IP-based geolocation as fallback


## 📦 Project Structure

```
- api/
  - ApiEndpoints.kt       // Defines API endpoints including current and forecast weather
  - Klient.kt             // Sets up the HTTP client
  - WeatherDao.kt         // DAO for weather data operations
  - WeatherForecastDao.kt // DAO for forecast operations

- db/
  - WeatherDatabase.kt           // Room database setup
  - WeatherDataTransaction.kt    // Handles DB transactions

- local/
  - Converters.kt        // Type converters for Room
  - PREF.kt              // SharedPreferences helper
  - UserDataStore.kt     // Manages user data in a datastore

- model/
  - CurrentWeatherEntity.kt     // Entity for current weather data
  - ErrorResponse.kt            // Models the API error response
  - ForecastWeatherEntity.kt    // Entity for forecast weather
  - GeoLocationResponse.kt      // Models the geolocation response

- repository/
  - Repository.kt        // Handles data access from API and DB

- util/
  - LocationHelper.kt         // Helps with location retrieval
  - networkBoundResource.kt   // Handles data caching logic
  - Resource.kt               // Wrapper for API response states

- view/
  - activity/
    - BaseActivity.kt        // Base class for activities
    - SignInActivity.kt      // Login screen
    - WeatherActivity.kt     // Main weather display UI

  - fragment/
    - ViewBindingAdapter.kt  // Binds data to views in layout XML
    - WeatherIcons.kt        // Maps weather codes to icons

  - viewmodel/
    - SignInViewModel.kt     // ViewModel for handling sign-in logic
    - WeatherViewModel.kt    // ViewModel for weather data operations
```

## 🛠️ API Endpoints
```kotlin
object ApiEndpoints {
    const val BASE_URL_WEATHER = "https://api.openweathermap.org/data/"
    const val GEOLOCATION_FROM_IP = "https://ipwho.is/"

    fun currentWeather(lat: Double, lon: Double, apiKey: String): String =
        "${BASE_URL_WEATHER}2.5/weather?lat=$lat&lon=$lon&appid=$apiKey&units=metric"

    fun forecastWeather(lat: Double, lon: Double, apiKey: String): String =
        "${BASE_URL_WEATHER}3.0/onecall?lat=$lat&lon=$lon&exclude=minutely,hourly,alerts,current&appid=$apiKey&units=metric"

    fun weatherIconUrl(iconCode: String): String =
        "https://openweathermap.org/img/wn/${iconCode}@2x.png"
}
```

## 📲 Dependencies

- Ktor
- Room
- Kotlin Coroutines + Flow
- Kotlin Serialization
- DataStore
- Compose UI
- ViewModel + LiveData
- Coil

## 📸 Screenshot
<p align="center">
    <img src="/screenshot/current_weather.png" alt="Current Weather" width="250" style="margin-right: 20px;"/>
    <img src="/screenshot/weather_forecast.png" alt="Weather Forecast" width="250" style="margin-right: 20px;"/>
    <img src="/screenshot/login_screen.png" alt="Login Screen" width="250" style="margin-right: 20px;"/>
</p>
