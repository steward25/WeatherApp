package com.stewardapostol.weatherapp.view.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.stewardapostol.weatherapp.data.model.DailyForecast
import com.stewardapostol.weatherapp.data.model.ForecastWeatherEntity
import com.stewardapostol.weatherapp.viewmodel.WeatherViewModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt
import coil.compose.AsyncImage
import com.stewardapostol.weatherapp.data.api.ApiEndpoints

class WeatherForecastFragment : Fragment() {

    private lateinit var viewModel: WeatherViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel = ViewModelProvider(requireParentFragment())[WeatherViewModel::class.java]

        return ComposeView(requireContext()).apply {
            setContent {
                WeatherForecastScreen(viewModel = viewModel)
            }
        }
    }
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
        modifier = Modifier.fillMaxSize(),
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