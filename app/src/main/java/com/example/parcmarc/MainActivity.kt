package com.example.parcmarc

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng

class MainActivity : PermittedActivity() {
    val hasLocationPermissions
        get() = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        val locationPermissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        requestPermissions(locationPermissions, 100, {
            promptForGPS()
        }, {
            Toast.makeText(this, "GPS not permitted. You will not be able to store park images.", Toast.LENGTH_LONG).show()
        })

        val storagePermissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        requestPermissions(storagePermissions, 100, {
        }, {
            Toast.makeText(this, "Unable to store photos.", Toast.LENGTH_LONG).show()
        })

        val darkModeValues = resources.getStringArray(R.array.dark_mode_values)
        // The apps theme is decided depending upon the saved preferences on app startup
        when (PreferenceManager.getDefaultSharedPreferences(this)
            .getString(getString(R.string.dark_mode), getString(R.string.dark_mode_def_value))) {
            darkModeValues[0] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            darkModeValues[1] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            darkModeValues[2] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            darkModeValues[3] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
        }



        createNotificationChannel()

    }

    private fun promptForGPS() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder(this).apply {
                setMessage("GPS is not enabled on your device. Enable it in the location settings.")
                setPositiveButton("Settings") { _, _ ->
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
                setNegativeButton("Cancel") { _, _ -> }
                show()
            }
        }
    }

    // Create a notification channel to send a daily reminder to use the app.
    private fun createNotificationChannel() {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(Notification.CATEGORY_REMINDER, "Daily reminders", importance).apply {
            description = "Send daily reminders use the app"
        }
        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

    }





//    @SuppressLint("MissingPermission")
//    private fun queryLocationForUnlock() {
//        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
//
//        val listener = object : LocationListener {
//            override fun onLocationChanged(location: Location) {
//                // TODO Do something with the Location
//
//                locationManager.removeUpdates(this)
//            }
//
//            override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}
//            override fun onProviderEnabled(provider: String) {}
//            override fun onProviderDisabled(provider: String) {}
//        }
//
//        if (hasLocationPermissions) {
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, listener)
//        }
//    }

}