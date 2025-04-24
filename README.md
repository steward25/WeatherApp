
# 🌦️ WeatherApp

A simple weather forecasting Android app using OpenWeatherMap API.

## 🔑 Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/your-username/weatherapp.git
cd weatherapp
```

### 2. Add your API key

You will need an API key from OpenWeatherMap.

Create a file named `local.properties` in the root of your project (if not already there).  
Add the following line:

```properties
OPEN_WEATHER_API_KEY=your_api_key_here
```

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

## 📸 Screenshot

### Current Weather
![Home Screen](screenshot/current_weather.png)

### Forecast Screen
![Forecast Screen](screenshot/weather_forecast.png)

