package com.stewardapostol.weatherapp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

object UserDataStore {

    val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user")

    val USERNAME = stringPreferencesKey("username")
    val PASSWORD = stringPreferencesKey("password")


    suspend fun Context.saveUserDataStore(pref: PREF) {
        coroutineScope {
            launch(Dispatchers.IO) {
                userDataStore.edit { datastore ->
                    pref.apply {
                        datastore[UserDataStore.USERNAME] = USERNAME
                        datastore[UserDataStore.PASSWORD] = PASSWORD
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
            }
        }
    }


    suspend fun Context.readUserDataStore(callback: suspend (Flow<PREF>) -> Unit) {
        callback.invoke(userDataStore.data.map { pref ->
            PREF.apply {
                USERNAME = pref[UserDataStore.USERNAME] ?: ""
                PASSWORD = pref[UserDataStore.PASSWORD] ?: ""
            }
        })
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