package com.example.ama_geofence
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DateAndTime {
    companion object{
        val dateAndTime: String
            get() {
                return form("yyyy-MM-dd HH:mm:ss")
            }

        val date: String
            get() {
                return form("yyyy-MM-dd")
            }

        val time: String
            get() {
                return form("HH:mm:ss")
            }

        private fun form(format: String): String{
            return LocalDateTime.now().format(DateTimeFormatter.ofPattern(format))
        }
    }
}