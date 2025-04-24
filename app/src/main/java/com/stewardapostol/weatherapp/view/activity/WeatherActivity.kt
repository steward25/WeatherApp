package com.stewardapostol.weatherapp.view.activity

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.stewardapostol.weatherapp.R
import com.stewardapostol.weatherapp.databinding.ActivityLayoutBinding
import com.stewardapostol.weatherapp.view.fragment.WeatherFragment
import com.stewardapostol.weatherapp.util.LocationHelper

class WeatherActivity : BaseActivity<ActivityLayoutBinding>() {

    private val LOCATION_PERMISSION_REQUEST_CODE = 100

    override fun activityLayout(): Int = R.layout.activity_layout

    override fun setContent() {
        super.setContent()
        startLocationUpdates()
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
        } else {
            getCityAndStartFragment()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCityAndStartFragment()
            } else {
                // Permission denied
                requestLocationPermission()
            }
        }
    }

    private fun getCityAndStartFragment() {
        // Using LocationHelper to get the location
        LocationHelper(this).getBestAvailableLocation { location ->
            location?.let {
                // Get the city using the location
                val city = getCityFromLocation(it)
                runOnUiThread {
                    Toast.makeText(this, "Current City: $city", Toast.LENGTH_LONG).show()
                }
            }
            startFragment()
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

    private fun startFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.parent, WeatherFragment())
            .commitAllowingStateLoss()
    }

    fun startLocationUpdates() {
        requestLocationPermission()
    }
}
