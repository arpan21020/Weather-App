import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.runtime.*
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Manages all location related tasks for the app.
 */
class UserLocation(activity: Activity) {
    lateinit var locationCallback: LocationCallback
    val activity:Activity=activity
    //The main entry point for interacting with the Fused Location Provider
    lateinit var locationProvider: FusedLocationProviderClient

    val REQUEST_LOCATION_PERMISSION = 123

    @SuppressLint("MissingPermission")
    @Composable
    fun getUserLocation(context: Context): LatandLong {

        // The Fused Location Provider provides access to location APIs.
        locationProvider = LocationServices.getFusedLocationProviderClient(context)

        var currentUserLocation by remember { mutableStateOf(LatandLong()) }

        DisposableEffect(key1 = locationProvider) {
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {

                    /**
                     * Option 1
                     * This option returns the locations computed, ordered from oldest to newest.
                     * */
                    for (location in result.locations) {
                        // Update data class with location data
                        currentUserLocation = LatandLong(location.latitude, location.longitude)
                        Log.d("LOCATION_TAG", "${location.latitude},${location.longitude}")
                    }


                    /**
                     * Option 2
                     * This option returns the most recent historical location currently available.
                     * Will return null if no historical location is available
                     * */
                    locationProvider.lastLocation
                        .addOnSuccessListener { location ->
                            location?.let {
                                val lat = location.latitude
                                val long = location.longitude
                                // Update data class with location data
                                currentUserLocation = LatandLong(latitude = lat, longitude = long)
                            }
                        }
                        .addOnFailureListener {
                            Log.e("Location_error", "${it.message}")
                        }

                }
            }
            if (hasPermissions(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION,

                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            ) {
                locationUpdate()
            } else {
                askPermissions(
                    activity, REQUEST_LOCATION_PERMISSION, Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            }

            onDispose {
                stopLocationUpdate()
            }
        }
        //
        return currentUserLocation

    }

    fun hasPermissions(context: Context, vararg permissions: String): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }
    fun askPermissions(activity:Activity, requestCode: Int, vararg permissions: String) {
        ActivityCompat.requestPermissions(
            activity,
            permissions,
            requestCode
        )
    }

    fun stopLocationUpdate() {
        try {
            //Removes all location updates for the given callback.
            val removeTask = locationProvider.removeLocationUpdates(locationCallback)
            removeTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("LOCATION_TAG", "Location Callback removed.")
                } else {
                    Log.d("LOCATION_TAG", "Failed to remove Location Callback.")
                }
            }
        } catch (se: SecurityException) {
            Log.e("LOCATION_TAG", "Failed to remove Location Callback.. $se")
        }
    }

    @SuppressLint("MissingPermission")
    fun locationUpdate() {
        locationCallback.let {
            //An encapsulation of various parameters for requesting
            // location through FusedLocationProviderClient.
            val locationRequest: LocationRequest =
                LocationRequest.create().apply {
                    interval = TimeUnit.SECONDS.toMillis(60)
                    fastestInterval = TimeUnit.SECONDS.toMillis(30)
                    maxWaitTime = TimeUnit.MINUTES.toMillis(2)
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                }
            //use FusedLocationProviderClient to request location update
            locationProvider.requestLocationUpdates(
                locationRequest,
                it,
                Looper.getMainLooper()
            )
        }

    }

    data class LatandLong(
        val latitude: Double = 0.0,
        val longitude: Double = 0.0
    )


    fun getReadableLocation(latitude: Double, longitude: Double, context: Context): String {
        var addressText = ""
        val geocoder = Geocoder(context, Locale.getDefault())

        try {

            val addresses = geocoder.getFromLocation(latitude, longitude, 1)

            if (addresses?.isNotEmpty() == true) {
                val address = addresses[0]
                addressText = "${address.getAddressLine(0)}, ${address.locality}"
                // Use the addressText in your app
                Log.d("geolocation", addressText)
            }

        } catch (e: IOException) {
            Log.d("geolocation", e.message.toString())

        }

        return addressText

    }

}

//A callback for receiving notifications from the FusedLocationProviderClient.

