package com.stewardapostol.weatherapp.view.composable

import android.annotation.SuppressLint
import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material3.placeholder
import com.google.accompanist.placeholder.material3.shimmer
import com.stewardapostol.weatherapp.BuildConfig
import com.stewardapostol.weatherapp.R
import com.stewardapostol.weatherapp.data.api.ApiEndpoints
import com.stewardapostol.weatherapp.data.local.UserDataStore.getCredentials
import com.stewardapostol.weatherapp.data.model.CurrentWeatherEntity
import com.stewardapostol.weatherapp.data.model.DailyForecast
import com.stewardapostol.weatherapp.data.model.ForecastWeatherEntity
import com.stewardapostol.weatherapp.view.WeatherIcons
import com.stewardapostol.weatherapp.viewmodel.AuthViewModel
import com.stewardapostol.weatherapp.viewmodel.WeatherViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

@Composable
fun WeatherScreen(
    weatherViewModel: WeatherViewModel,
    authViewModel: AuthViewModel,
    onSignOutClick: () -> Unit
) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(pageCount = { 2 })
    val scope = rememberCoroutineScope()


    val locMessage by weatherViewModel.locMessage.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        weatherViewModel.fetchAllWeatherData(BuildConfig.API_KEY)
        weatherViewModel.fetchForecastFromAssets()
    }

    locMessage?.let {
        Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF7F7FD5), Color(0xFF86A8E7), Color(0xFF91EAE4))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopBar(authViewModel) {
                onSignOutClick()
            }

            TabRow(selectedTabIndex = pagerState.currentPage) {
                Tab(
                    selected = pagerState.currentPage == 0,
                    onClick = { scope.launch { pagerState.scrollToPage(0) } },
                    text = { Text("Current Weather") }
                )
                Tab(
                    selected = pagerState.currentPage == 1,
                    onClick = { scope.launch { pagerState.scrollToPage(1) } },
                    text = { Text("Weather List") }
                )
            }

            HorizontalPager(state = pagerState) { page ->
                when (page) {
                    0 -> CurrentWeatherScreen(weatherViewModel)
                    1 -> WeatherForecastScreen(weatherViewModel)
                }
            }
        }
    }
}

@Composable
fun TopBar(authViewModel: AuthViewModel, onSignOutClick: () -> Unit) {
    var credentials by remember { mutableStateOf(Triple<String?, String?, Boolean?>("", "", true)) }

    LaunchedEffect(Unit) {
        credentials = authViewModel.getApplication<Application>().getCredentials()
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = "User Icon",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = credentials.first ?: "",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Button(onClick = onSignOutClick) {
            Icon(
                imageVector = Icons.Outlined.ExitToApp,
                contentDescription = "Sign Out",
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
            Text("Sign Out")
        }
    }
}

@Composable
fun CurrentWeatherScreen(viewModel: WeatherViewModel) {
    val currentWeather by viewModel.weatherData.collectAsStateWithLifecycle()
    val weather = currentWeather as? CurrentWeatherEntity

    weather?.let {
        val context = LocalContext.current
        val country = remember(weather.sys?.country) {
            Locale("", weather.sys?.country.orEmpty()).displayCountry
        }

        val location = "${weather.cityName}, $country"
        val temperature = weather.main?.tempMax?.let {
            context.getString(R.string.temperature_unit, it)
        } ?: "--"

        val sunrise = convertTimestampToTime(weather.sys?.sunrise, weather.timezone)
        val sunset = convertTimestampToTime(weather.sys?.sunset, weather.timezone)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(align = Alignment.Top)
                .padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentHeight(align = Alignment.CenterVertically)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.67f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = location,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    WeatherIcon(iconCode = weather.weather?.firstOrNull()?.icon)

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = temperature,
                        fontSize = 48.sp,
                        color = Color.Gray,
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(end = 32.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.sunrise),
                                fontSize = 16.sp,
                                color = Color.Gray,
                            )
                            Text(text = sunrise, color = Color.Gray)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = stringResource(R.string.sunset),
                                fontSize = 16.sp,
                                color = Color.Gray,
                            )
                            Text(text = sunset, color = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    WeatherForecastScreen(viewModel)
                }
            }
        }
    } ?: run {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                color = Color.Blue,
                strokeWidth = 4.dp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("Loading weather data...", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun WeatherIcon(iconCode: String?) {
    val context = LocalContext.current
    val imageUrl = remember(iconCode) {
        iconCode?.let { WeatherIcons.getWeatherIcons(context, it) }
    }

    AsyncImage(
        model = imageUrl ?: R.drawable.ic_weather_placeholder,
        contentDescription = iconCode.toString(),
        modifier = Modifier.size(120.dp),
        contentScale = ContentScale.Fit,
    )
}

@Composable
fun WeatherForecastScreen(viewModel: WeatherViewModel) {
    val forecast by viewModel.weatherForecastData.collectAsState(initial = null)

    forecast?.let { weather ->
        WeatherList(weather = weather as? ForecastWeatherEntity)
    } ?: run {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 4.dp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("Loading weather data...", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun WeatherList(weather: ForecastWeatherEntity?) {
    val dailyList = weather?.daily.orEmpty()

    val overallMinTemp = remember(dailyList) {
        dailyList.mapNotNull { it.temp?.min }.minOrNull() ?: 0.0
    }

    val overallMaxTemp = remember(dailyList) {
        dailyList.mapNotNull { it.temp?.max }.maxOrNull() ?: 0.0
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(dailyList) { forecast ->
            DailyForecastItem(
                forecast = forecast,
                overallMinTemp = overallMinTemp,
                overallMaxTemp = overallMaxTemp
            )
        }
    }
}


@SuppressLint("NewApi")
@Composable
fun DailyForecastItem(
    forecast: DailyForecast,
    overallMinTemp: Double,
    overallMaxTemp: Double
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .border(1.dp, Color.LightGray, MaterialTheme.shapes.medium)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val instant = forecast.dateTime?.let { Instant.ofEpochSecond(it.toLong()) }
        val dateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC)
        val dayOfWeek = dateTime.format(DateTimeFormatter.ofPattern("EEE", Locale.getDefault()))

        Text(
            text = dayOfWeek,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            modifier = Modifier.weight(0.15f)
        )
        Spacer(modifier = Modifier.width(8.dp))

        val iconCode = forecast.weather?.firstOrNull()?.icon
        val imageUrl = remember(iconCode) {
            iconCode?.let {
                ApiEndpoints.weatherIcon(it)
            }
        }

        AsyncImage(
            model = imageUrl,
            contentDescription = forecast.weather?.firstOrNull()?.description,
            modifier = Modifier
                .size(32.dp)
                .weight(0.15f),
            placeholder = painterResource(id = android.R.drawable.ic_menu_gallery),
            error = painterResource(id = android.R.drawable.ic_menu_report_image)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "${forecast.temp?.min?.roundToInt()}° C",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.weight(0.15f),
            textAlign = TextAlign.End
        )
        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .height(8.dp)
                .weight(0.4f)
                .background(Color.LightGray)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val minWeight = forecast.temp?.min?.let {
                    calculateTempBarWeight(
                        it,
                        overallMinTemp,
                        overallMaxTemp
                    )
                }
                val maxWeight = 1f - minWeight!!

                Box(
                    modifier = Modifier
                        .background(Color(0xFFFF9800))
                        .weight(if (minWeight > 0) minWeight else 0.001f)
                        .fillMaxHeight()
                )
                Box(
                    modifier = Modifier
                        .background(Color(0xFFF44336))
                        .weight(if (maxWeight > 0) maxWeight else 0.001f)
                        .fillMaxHeight()
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "${forecast.temp?.max?.roundToInt()}° C",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(0.15f),
            textAlign = TextAlign.End
        )
    }
}

fun calculateTempBarWeight(
    currentTemp: Double,
    overallMin: Double,
    overallMax: Double
): Float {
    val tempRange = overallMax - overallMin
    return if (tempRange > 0) {
        ((currentTemp - overallMin) / tempRange).coerceIn(0.0, 1.0).toFloat()
    } else {
        0.5f
    }
}

fun convertTimestampToTime(timestamp: Long?, timezoneOffsetSeconds: Int?): String {
    return if (timestamp != null && timezoneOffsetSeconds != null) {
        val timezoneOffsetMillis = TimeUnit.SECONDS.toMillis(timezoneOffsetSeconds.toLong())
        val date = Date((timestamp + timezoneOffsetMillis) * 1000)
        val sdf = SimpleDateFormat("h:mm", Locale.getDefault())
        sdf.format(date).uppercase(Locale.getDefault()).trim()
    } else {
        "--:--"
    }
}