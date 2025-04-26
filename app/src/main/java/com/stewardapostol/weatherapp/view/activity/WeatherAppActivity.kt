package com.stewardapostol.weatherapp.view.activity

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.stewardapostol.weatherapp.data.local.PREF
import com.stewardapostol.weatherapp.util.LocationHelper
import com.stewardapostol.weatherapp.view.composable.WeatherScreen
import com.stewardapostol.weatherapp.viewmodel.AuthViewModel
import com.stewardapostol.weatherapp.viewmodel.WeatherViewModel

class WeatherAppActivity : AppCompatActivity() {

    private val LOCATION_PERMISSION_REQUEST_CODE = 100
    var authViewModel: AuthViewModel? = null


    override fun onStart() {
        super.onStart()
        requestLocationPermission()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            authViewModel =
                viewModel(factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return AuthViewModel(application) as T
                    }
                })
            val isLoggedIn by authViewModel!!.isLoggedIn.collectAsState()

            val navController = rememberNavController()
            NavHost(navController, startDestination = "auth") {
                composable("auth") {
                    if (!isLoggedIn) {
                        AuthScreen(authViewModel!!) {
                            navController.navigate("weather_screen") {
                                popUpTo("auth") { inclusive = true }
                            }
                        }
                    } else {
                        navController.navigate("weather_screen") {
                            popUpTo("auth") { inclusive = true }
                        }
                    }
                }
                composable("weather_screen") {
                    // Get the weatherViewModel using viewModel() with factory
                    val weatherViewModel: WeatherViewModel = viewModel(
                        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(LocalContext.current.applicationContext as Application)
                    )
                    WeatherScreen(
                        weatherViewModel = weatherViewModel,
                        authViewModel = authViewModel!!
                    ) {
                        authViewModel?.logout()
                        navController.navigate("auth") {
                            popUpTo("weather_screen") { inclusive = true }
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        authViewModel?.checkLoggedInStatus()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                LocationHelper(this).getBestAvailableLocation { location ->
                    val city = location?.let { getCityFromLocation(it) }
                    runOnUiThread {
                        Toast.makeText(this, "City: $city", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                requestLocationPermission()
            }
        }
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun getCityFromLocation(location: Location): String {
        val geocoder = android.location.Geocoder(this)
        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
        return if (addresses.isNullOrEmpty()) {
            "Unknown"
        } else {
            addresses[0].locality ?: "Unknown"
        }
    }
}

@Composable
fun AuthScreen(viewModel: AuthViewModel, onLoginSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val state by viewModel.loginState.collectAsState()
    val loginSuccess by viewModel.loginSuccess.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(state) {
        if (state == "Registered successfully!") {
            Toast.makeText(context, "Registered successfully!", Toast.LENGTH_SHORT).show()
            email = ""
            password = ""
        }
    }

    LaunchedEffect(loginSuccess) {
        if (loginSuccess) {
            email = ""
            password = ""
            onLoginSuccess()
        }
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
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Text(
                text = "\uD83C\uDF26\uFE0F Your Weather",
                style = TextStyle(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                ),
                modifier = Modifier.padding(16.dp)
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") })
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation()
                    )


                    Spacer(modifier = Modifier.height(16.dp))

                    Row {
                        Button(onClick = {
                            viewModel.register(PREF.apply {
                                USERNAME = email
                                PASSWORD = password
                            })
                        }) {
                            Text("Register")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = { viewModel.login(email, password) }) {
                            Text("Login")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = state)
                }
            }
        }
    }
}
