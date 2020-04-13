package com.example.ama_geofence

import com.example.ama_geofence.location.LocationData
import com.google.android.gms.location.*

class CurrentState {
    companion object{
        var configuration: Configuration? = null
        var location: LocationData? = null
        var activityType: Int = DetectedActivity.STILL
    }
}