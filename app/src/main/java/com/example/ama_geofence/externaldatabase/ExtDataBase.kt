package com.example.ama_geofence.externaldatabase

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.example.ama_geofence.Configuration
import com.example.ama_geofence.CurrentState
import com.example.ama_geofence.MainActivity
import com.example.ama_geofence.LogMessage
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ExtDataBase(private val context: Context) {
    private val database = FirebaseDatabase.getInstance().reference

    fun hasPermission(): Boolean{
        val permissionAccessCoarseLocationApproved = ActivityCompat
            .checkSelfPermission(context, Manifest.permission.INTERNET) ==
                PackageManager.PERMISSION_GRANTED

        if(permissionAccessCoarseLocationApproved) {
            MainActivity.log(LogMessage(text = "Internet permission granted"))
        }else{
            MainActivity.log(LogMessage(text = "Internet permission not granted"))
        }

        return permissionAccessCoarseLocationApproved
    }

    fun write(date: String) {
        database.child("users").child(CurrentState.configuration!!.objectId).child(date).setValue(CurrentState.location)
        MainActivity.log(LogMessage(text = "Location sent"))
    }

    fun readConfiguration(_userId: String) {
        val userId = if (_userId == "")  "??????" else _userId
        database.child("config").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val config = dataSnapshot.getValue(Configuration::class.java)
                if(config != null){
                    CurrentState.configuration = config
                    MainActivity.log(LogMessage(text = "Config loaded: " + CurrentState.configuration!!.name))
                }else{
                    MainActivity.log(LogMessage(text = "Config not found"))
                }
            }
        })
    }
}