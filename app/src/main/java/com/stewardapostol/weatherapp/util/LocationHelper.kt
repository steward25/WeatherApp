package com.stewardapostol.weatherapp.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.stewardapostol.weatherapp.data.api.Klient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LocationHelper(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    /**
     * Attempts to get the user's current location:
     * 1. IP-based geolocation (priority)
     * 2. FusedLocationProviderClient (fallback)
     * 3. LocationManager + NETWORK_PROVIDER (last resort)
     */
    fun getBestAvailableLocation( cs: (String?) -> Unit = {},callback: (Location?) -> Unit) {
        // First, try IP-based geolocation
        getLocationFromIP(cb ={
            cs.invoke(it)
        }) { ipLocation ->
            if (ipLocation != null) {
                // If IP-based location is available, return it
                callback(ipLocation)
            } else {
                // If IP-based location fails, fall back to FusedLocationProviderClient
                getLocationFromFusedProvider(callback)
            }
        }
    }

    private fun getLocationFromIP( cb: (String?) -> Unit = {},callback: (Location?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val coords = Klient.getLatLongFromPublicIP { error ->
                Log.e("LocationHelper", "IP Geolocation failed: ${error.message}")
            }
            Log.e("LocationHelper", "geoLocationFromIp: $coords")
            cb("geoLocationFromIp: $coords")
            withContext(Dispatchers.Main) {
                if (coords != null) {
                    val (lat, lon) = coords
                    val ipLocation = Location("ip-api").apply {
                        latitude = lat
                        longitude = lon
                    }
                    Log.e("LocationHelper", "geoLocationFromIp: ipLocation =$ipLocation")
                    cb("geoLocationFromIp: ipLocation =$ipLocation")
                    callback(ipLocation)
                } else {
                    callback(null)
                }
            }
        }
    }

    private fun getLocationFromFusedProvider(callback: (Location?) -> Unit) {
        // Check for location permissions
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            callback(null)
            return
        }

        // Try to get location from FusedLocationProviderClient
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    // If FusedLocationProviderClient gives location, return it
                    callback(location)
                } else {
                    // If FusedLocationProviderClient fails, fall back to Network Provider
                    getLocationFromNetworkProvider(callback)
                }
            }
            .addOnFailureListener {
                // If FusedLocationProviderClient fails, fall back to Network Provider
                getLocationFromNetworkProvider(callback)
            }
    }

    private fun getLocationFromNetworkProvider(callback: (Location?) -> Unit) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Check for location permissions
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            callback(null)
            return
        }

        // Get location from Network Provider (fallback)
        val location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        if (location != null) {
            // If Network Provider gives location, return it
            callback(location)
        } else {
            callback(null)
        }
    }
}
