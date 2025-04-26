package com.stewardapostol.weatherapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.stewardapostol.weatherapp.data.local.PREF
import com.stewardapostol.weatherapp.data.local.UserDataStore.getCredentials
import com.stewardapostol.weatherapp.data.local.UserDataStore.saveUserDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    val loginState = MutableStateFlow("")
    val loginSuccess = MutableStateFlow(false)
    val isLoggedIn = MutableStateFlow(false)


    fun checkLoggedInStatus() {
        viewModelScope.launch {
            val (savedEmail, savedPassword, state) = getApplication<Application>().getCredentials()
            state?.let {
                if (state) {
                    isLoggedIn.value = true
                }
            }

            loginState.value = ""
        }
    }

    fun register(pref: PREF) {
        viewModelScope.launch {
            getApplication<Application>().saveUserDataStore(pref)
            loginState.value = "Registered successfully!"
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val (savedEmail, savedPassword, state) = getApplication<Application>().getCredentials()
            state?.let {
                if (!state) {
                    if (email == savedEmail && password == savedPassword) {
                        loginState.value = "Login successful!"
                        loginSuccess.value = true
                    } else {
                        loginState.value = "Invalid credentials!"
                        loginSuccess.value = false
                    }
                } else {
                    loginSuccess.value = true
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            loginState.value = ""
            loginSuccess.value = false

            val (savedEmail, savedPassword, saveState) = getApplication<Application>().getCredentials()
            getApplication<Application>().saveUserDataStore(
                PREF.apply {
                    USERNAME = savedEmail!!
                    PASSWORD = savedPassword!!
                    STATE = saveState!!
                })

        }
    }
}
