package com.example.ama_geofence.location

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.example.ama_geofence.*
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task

class Location(private val context: Context) {
    enum class Status {
        STOPPED,
        STARTED
    }

    var status: Status = Status.STOPPED
    private var fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    fun getLastLocation(){
        fusedLocationClient.lastLocation.addOnSuccessListener {
                CurrentState.location = convertToLocationData(it)
            }
    }

    fun startLocationUpdates() {
        val locationRequest = CurrentState.configuration?.let { createLocationRequest(it) }

        // Check whether location settings are satisfied
        val client = LocationServices.getSettingsClient(context)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(LocationSettingsRequest.Builder().build())

        task.addOnSuccessListener {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper())
            status = Status.STARTED
            MainActivity.log(LogMessage(text = "Location updates acivated"))
        }

        task.addOnFailureListener {
            MainActivity.log(LogMessage(text = "Localion updates activation failed"))
        }
    }


    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        status = Status.STOPPED
        MainActivity.log(LogMessage(text = "Location updates deacivated"))
    }

    fun hasPermission(): Boolean{
        val permissionAccessCoarseLocationApproved = ActivityCompat
            .checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED

        if(permissionAccessCoarseLocationApproved) {
            MainActivity.log(LogMessage(text = "Location permission granted"))
        }else{
            MainActivity.log(LogMessage(text = "Location permission not granted"))
        }

        return permissionAccessCoarseLocationApproved
    }

    fun givePermission(){
        ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return
            for (location in locationResult.locations) {
                CurrentState.location = convertToLocationData(location)
            }
        }
    }

    private fun createLocationRequest(config: Configuration): LocationRequest {
        return LocationRequest.create()?.apply {
            interval = config.interval
            fastestInterval = config.interval
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }!!
    }

    private fun convertToLocationData(location: android.location.Location): LocationData{
        return LocationData(
            accuracy = location.accuracy,
            altitude = location.altitude,
            bearing = location.bearing,
            latitude = location.latitude,
            longitude = location.longitude
        )
    }
}