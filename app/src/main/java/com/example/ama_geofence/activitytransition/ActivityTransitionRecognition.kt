package com.example.ama_geofence.activitytransition

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.ama_geofence.CurrentState
import com.example.ama_geofence.MainActivity
import com.example.ama_geofence.LogMessage
import com.example.ama_geofence.location.Location
import com.google.android.gms.location.*

class ActivityTransitionRecognition(context: Context) {
    private val activityRecognitionClient: ActivityRecognitionClient = ActivityRecognition.getClient(context)
    private var transitionPendingIntent: PendingIntent? = null
    private var mContext = context

    companion object{
        fun convertActivityType(activityType: Int): String {
            return when (activityType) {
                DetectedActivity.IN_VEHICLE -> "IN VEHICLE"
                DetectedActivity.ON_BICYCLE -> "ON BICYCLE"
                DetectedActivity.ON_FOOT -> "ON FOOT"
                DetectedActivity.STILL -> "STILL"
                DetectedActivity.UNKNOWN -> "UNKNOWN"
                DetectedActivity.TILTING -> "TITLING"
                DetectedActivity.WALKING -> "WALKING"
                DetectedActivity.RUNNING -> "RUNNING"
                else -> "ERROR"
            }
        }

        fun convertTransitionType(transitionType: Int): String {
            return when (transitionType) {
                ActivityTransition.ACTIVITY_TRANSITION_ENTER -> "ENTER"
                ActivityTransition.ACTIVITY_TRANSITION_EXIT -> "EXIT"
                else -> "ERROR"
            }
        }
    }

    fun start(){
        val activityTransitionRequest = ActivityTransitionRequest(getTransitions())
        val intent = Intent(mContext, TransitionBroadcastReceiver::class.java)
        transitionPendingIntent = PendingIntent.getBroadcast(
            mContext,
            1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val task = this.activityRecognitionClient.requestActivityTransitionUpdates(
            activityTransitionRequest, transitionPendingIntent
        )

        task.addOnSuccessListener {
            MainActivity.log(LogMessage(text = "Transition update set up"))
        }

        task.addOnFailureListener{
            MainActivity.log(LogMessage(text = "Transition update failed to set up"))
        }
    }

    fun stop(){
        val task = ActivityRecognition.getClient(mContext)
            .removeActivityTransitionUpdates(transitionPendingIntent)

        task.addOnSuccessListener {
            MainActivity.log(LogMessage(text = "Transition update deactivated"))
            this.transitionPendingIntent!!.cancel()
        }

        task.addOnFailureListener {
            MainActivity.log(LogMessage(text = "Transition update deactivation failed"))
        }
    }

    // prepare transitions types to recognize
    private fun getTransitions(): MutableList<ActivityTransition>
    {
        val transitions = mutableListOf<ActivityTransition>()

        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build()

        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build()

        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build()

        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build()

        return transitions
    }

    class TransitionBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (ActivityTransitionResult.hasResult(intent)) {
                val result = ActivityTransitionResult.extractResult(intent)
                for (event in result!!.transitionEvents) {

                    CurrentState.activityType = event.activityType

                    if(CurrentState.activityType != DetectedActivity.STILL){
                        if(MainActivity.location.status == Location.Status.STOPPED)
                            MainActivity.location.startLocationUpdates()
                    }else{
                        if(MainActivity.location.status == Location.Status.STARTED)
                            MainActivity.location.stopLocationUpdates()
                    }


                    val activityType = convertActivityType(event.activityType)
                    val transitionType = convertTransitionType(event.transitionType)

                    MainActivity.log(
                        LogMessage(
                            text = "Transition: $activityType ($transitionType)"
                        )
                    )
                }
            }
        }
    }
}