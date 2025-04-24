package com.stewardapostol.weatherapp.view.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stewardapostol.weatherapp.util.LocationHelper
import com.stewardapostol.weatherapp.viewmodel.Auth
import com.stewardapostol.weatherapp.viewmodel.SignInViewModel

class SignInActivity : AppCompatActivity() {

    private val viewModel: SignInViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SignInScreen(viewModel = viewModel)
        }

        viewModel.signInSuccess.observe(this) { success ->
            when (success!!) {
                is Auth.SignIn -> {
                    runOnUiThread {
                        Toast.makeText(this, "Welcome", Toast.LENGTH_SHORT).show()
                        goToWeatherActivity()
                    }
                }

                is Auth.Register -> runOnUiThread {
                    Toast.makeText(this, "Registered", Toast.LENGTH_SHORT).show()
                }

                else -> Toast.makeText(this, "Sign-in failed", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.errorMessage.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        viewModel.checkIfUserIsSignedIn()

        LocationHelper(this).getBestAvailableLocation(cs = {
            runOnUiThread {
                Toast.makeText(this, "Location: " + it, Toast.LENGTH_LONG).show()
            }
        }) {}
    }

    private fun goToWeatherActivity() {
        startActivity(Intent(this, WeatherActivity::class.java))
        finish()
    }
}

@Composable
fun SignInScreen(viewModel: SignInViewModel) {
    var username by remember { mutableStateOf(viewModel.username.value) }
    var password by remember { mutableStateOf(viewModel.password.value) }

    // Observe registerSuccess flag
    val registerSuccess by viewModel.registerSuccess.collectAsState()

    LaunchedEffect(registerSuccess) {
        if (registerSuccess) {
            username = ""
            password = ""
            viewModel.updateUsername("")
            viewModel.updatePassword("")
            viewModel.clearRegisterFlag()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Username",
            fontSize = 16.sp,
            modifier = Modifier.align(Alignment.Start)
        )

        username?.let {
            TextField(
                value = it,
                onValueChange = {
                    username = it
                    viewModel.updateUsername(it)
                },
                label = { Text("Enter username") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                )
            )
        }

        Text(
            text = "Password",
            fontSize = 16.sp,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(top = 24.dp)
        )

        password?.let {
            TextField(
                value = it,
                onValueChange = {
                    password = it
                    viewModel.updatePassword(it)
                },
                label = { Text("Enter password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                )
            )
        }

        Button(
            onClick = { viewModel.onSignInClicked() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp)
        ) {
            Text(text = "Sign In", fontSize = 16.sp)
        }

        Button(
            onClick = { viewModel.onRegisterClicked() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text(text = "Register", fontSize = 16.sp)
        }
    }
}