package com.stewardapostol.weatherapp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

object UserDataStore {

    val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user")

    val USERNAME = stringPreferencesKey("username")
    val PASSWORD = stringPreferencesKey("password")
    val STATE  = booleanPreferencesKey("login_state")


    suspend fun Context.saveUserDataStore(pref: PREF) {
        coroutineScope {
            launch(Dispatchers.IO) {
                userDataStore.edit { datastore ->
                    pref.apply {
                        datastore[UserDataStore.USERNAME] = USERNAME
                        datastore[UserDataStore.PASSWORD] = PASSWORD
                        datastore[UserDataStore.STATE] = STATE
                    }
                }
            }
        }
    }

    fun Context.readUserDataStore(): Flow<PREF> {
        return userDataStore.data.map { preferences ->
            PREF.apply {
                USERNAME = preferences[UserDataStore.USERNAME] ?: ""
                PASSWORD = preferences[UserDataStore.PASSWORD] ?: ""
                STATE = preferences[UserDataStore.STATE] ?: false
            }
        }
    }


    suspend fun Context.readUserDataStore(callback: suspend (Flow<PREF>) -> Unit) {
        callback.invoke(userDataStore.data.map { pref ->
            PREF.apply {
                USERNAME = pref[UserDataStore.USERNAME] ?: ""
                PASSWORD = pref[UserDataStore.PASSWORD] ?: ""
                STATE = pref[UserDataStore.STATE] ?: false
            }
        })
    }


    suspend fun Context.getCredentials(): Triple<String?, String?, Boolean?> {
        val prefs = userDataStore.data.first()
        return Triple(prefs[UserDataStore.USERNAME], prefs[UserDataStore.PASSWORD], prefs[UserDataStore.STATE])
    }

    fun Context.readUserDataStore(
        dispatcher: CoroutineDispatcher,
        callback: suspend (PREF) -> Unit
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            readUserDataStore {
                it.collectLatest { pref ->
                    launch(dispatcher) {
                        callback.invoke(pref)
                    }
                }
            }
        }
    }

}