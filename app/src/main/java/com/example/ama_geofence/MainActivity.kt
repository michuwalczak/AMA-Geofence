package com.example.ama_geofence


import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ama_geofence.activitytransition.ActivityTransitionRecognition
import com.example.ama_geofence.externaldatabase.ExtDataBase
import com.example.ama_geofence.internaldatabase.IntDataBase
import com.example.ama_geofence.internaldatabase.IntDataBaseEntity
import com.example.ama_geofence.internaldatabase.IntLogDao
import com.example.ama_geofence.internaldatabase.LogViewModel
import com.example.ama_geofence.location.Location
import java.util.*


class MainActivity : AppCompatActivity() {

    companion object {
        lateinit var logViewModel: LogViewModel
        lateinit var location: Location
        lateinit var activityTransition: ActivityTransitionRecognition
        lateinit var externalDataBase: ExtDataBase

        fun log(logMessage: LogMessage) {
            val localMessage =
                IntDataBaseEntity(
                    id = 0,
                    date = logMessage.date,
                    time = logMessage.time,
                    text = logMessage.text
                )
            logViewModel.insert(localMessage)
        }
    }

    private val timer = Timer()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


       // val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
       // val adapter = LogListAdapter(this)
       // recyclerView.adapter = adapter
       // recyclerView.layoutManager = LinearLayoutManager(this)

        logViewModel = ViewModelProvider(this).get(LogViewModel::class.java)
       // logViewModel.allMessages.observe(this, Observer { messages ->
          //  messages?.let { adapter.setMessages(it) }
       // })


        val btnLoadConfig = findViewById<Button>(R.id.btnLoadConfig)
       // val btnStartLog = findViewById<Button>(R.id.btnStartLog)
        val btnClearLog = findViewById<Button>(R.id.btnClearLog)
        val txtUserId = findViewById<EditText>(R.id.txtUserId)

        location = Location (this)
        activityTransition = ActivityTransitionRecognition(this)
        externalDataBase = ExtDataBase(this)


        btnLoadConfig.setOnClickListener {
                if(externalDataBase.hasPermission() && isNetworkAvailable())
                    loadConfiguration(txtUserId.text.toString())
            val intent= Intent (this, SecondActivity::class.java)
            startActivity(intent)
        }
//
//        btnStartLog.setOnClickListener {
//                if(location.hasPermission()) {
//                    if (isLocationAvailable())
//                        startLogLocation()
//                }else{
//                    location.givePermission()
//                }
//            }


        btnClearLog.
            setOnClickListener {
                val intent= Intent (this, SecondActivity::class.java)
                startActivity(intent)

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        activityTransition.stop()
        location.stopLocationUpdates()
    }

    private fun loadConfiguration(userId: String) = externalDataBase.readConfiguration(userId)

//    private fun startLogLocation(){
//        location.getLastLocation()
//        activityTransition.start()
//
//        timer.schedule(object : TimerTask() {
//            override fun run() {
//                externalDataBase.write(DateAndTime.dateAndTime)
//            }
//        }, 5000, CurrentState.configuration!!.interval * 60 * 1000)
//    }

//    fun clearLog(){
//        logViewModel.deleteAll()
//    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo

        return if(activeNetworkInfo != null && activeNetworkInfo.isConnected){
            log(LogMessage(text = "Internet connection ON"))
            true
        }else{
            log(LogMessage(text = "Internet connection OFF"))
            false
        }
    }

//    private fun isLocationAvailable(): Boolean {
//        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
//
//        return if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
//            log(LogMessage(text = "Location service ON"))
//            true
//        }else{
//            log(LogMessage(text = "Location service OFF"))
//            false
//        }
//    }
}