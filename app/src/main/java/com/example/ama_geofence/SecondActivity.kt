package com.example.ama_geofence

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ama_geofence.MainActivity.Companion.logViewModel
import com.example.ama_geofence.internaldatabase.LogViewModel
import kotlinx.android.synthetic.main.activity_second.*
import java.util.*

class SecondActivity : AppCompatActivity() {
    private val timer = Timer()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val btnClearLog = findViewById<Button>(R.id.btnClearLog)
        val btnStartLog = findViewById<Button>(R.id.btnStartLog)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = LogListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        logViewModel = ViewModelProvider(this).get(LogViewModel::class.java)
        logViewModel.allMessages.observe(this, androidx.lifecycle.Observer { messages ->
            messages?.let { adapter.setMessages(it) }
        })

        val btnToMain = findViewById<Button>(R.id.btnToMain)
        btnToMain.setOnClickListener{
            val intent= Intent (this, MainActivity::class.java)
            startActivity(intent)
        }
        btnClearLog.
            setOnClickListener {
               clearLog()

            }

        btnStartLog.setOnClickListener {
            if(MainActivity.location.hasPermission()) {
                if (isLocationAvailable())
                    startLogLocation()
            }else{
                MainActivity.location.givePermission()
            }
        }
    }
    fun clearLog(){
        logViewModel.deleteAll()
    }
    private fun startLogLocation(){
        MainActivity.location.getLastLocation()
        MainActivity.activityTransition.start()

        timer.schedule(object : TimerTask() {
            override fun run() {
                MainActivity.externalDataBase.write(DateAndTime.dateAndTime)
            }
        }, 5000, CurrentState.configuration!!.interval * 60 * 1000)
    }
    private fun isLocationAvailable(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            MainActivity.log(LogMessage(text = "Location service ON"))
            true
        }else{
            MainActivity.log(LogMessage(text = "Location service OFF"))
            false
        }
    }
}
