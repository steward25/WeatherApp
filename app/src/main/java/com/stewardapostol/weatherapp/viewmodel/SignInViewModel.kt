package com.stewardapostol.weatherapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.stewardapostol.weatherapp.data.local.PREF
import com.stewardapostol.weatherapp.data.local.UserDataStore.readUserDataStore
import com.stewardapostol.weatherapp.data.local.UserDataStore.saveUserDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed class Auth {
    data class SignIn(val username: String, val password: String) : Auth()
    data class Register(val username: String, val password: String) : Auth()
}

class SignInViewModel(application: Application) : AndroidViewModel(application) {

    private val _username = MutableLiveData<String>("")
    val username: LiveData<String> get() = _username

    private val _password = MutableLiveData<String>("")
    val password: LiveData<String> get() = _password

    private val _signInSuccess = MutableLiveData<Auth>()
    val signInSuccess: LiveData<Auth> get() = _signInSuccess

    private val _registerSuccess = MutableStateFlow(false)
    val registerSuccess: StateFlow<Boolean> = _registerSuccess

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun checkIfUserIsSignedIn() {
        viewModelScope.launch(Dispatchers.IO) {
            getApplication<Application>().readUserDataStore (Dispatchers.IO){ pref ->
                if (pref.USERNAME.isNotEmpty() && pref.PASSWORD.isNotEmpty()) {
                    _signInSuccess.postValue(Auth.SignIn(pref.USERNAME, pref.PASSWORD))
                }
            }
        }
    }

    fun onSignInClicked() {
        val usernameValue = _username.value
        val passwordValue = _password.value

        if (usernameValue.isNullOrEmpty() || passwordValue.isNullOrEmpty()) {
            _errorMessage.postValue("Username or password cannot be empty")
            return
        }

        viewModelScope.launch(Dispatchers.Main) {
            var existingUsername = ""

            val userPref = getApplication<Application>().readUserDataStore().first()

            existingUsername = userPref.USERNAME

            if (existingUsername.isNotEmpty() && existingUsername != usernameValue) {
                _errorMessage.postValue("Another user is already signed in. Please log out first.")
            } else {
                PREF.apply {
                    USERNAME = usernameValue
                    PASSWORD = passwordValue
                }
                saveUserData(PREF)
                _signInSuccess.postValue(Auth.SignIn(PREF.USERNAME, PREF.PASSWORD))
            }
        }
    }

    private fun saveUserData(pref: PREF) {
        viewModelScope.launch(Dispatchers.IO) {
            getApplication<Application>().saveUserDataStore(pref)
        }
    }

    fun onRegisterClicked() {
        val usernameValue = _username.value
        val passwordValue = _password.value

        if (usernameValue.isNullOrEmpty() || passwordValue.isNullOrEmpty()) {
            _errorMessage.postValue("Username and password cannot be empty for registration.")
            return
        }

        viewModelScope.launch(Dispatchers.Main) {
            var existingUsername = ""
            getApplication<Application>().readUserDataStore { pref ->
                pref.collect{
                    existingUsername = it.USERNAME
                }
            }

            if (existingUsername.isNotEmpty() && existingUsername == usernameValue) {
                _errorMessage.postValue("Username already exists. Please choose a different one.")
            } else {
                PREF.apply {
                    USERNAME = usernameValue
                    PASSWORD = passwordValue
                }
                saveUserData(PREF)
                Log.d("SignInViewModel", "User registered with username: $usernameValue")
                _signInSuccess.postValue(Auth.Register(PREF.USERNAME, PREF.PASSWORD))
                _registerSuccess.value = true
            }
        }
    }

    fun updateUsername(username: String) {
        _username.value = username
    }

    fun updatePassword(password: String) {
        _password.value = password
    }

    fun clearRegisterFlag() {
        _registerSuccess.value = false
    }

}
