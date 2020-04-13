package com.example.ama_geofence.internaldatabase

import androidx.lifecycle.LiveData

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class LogRepository(private val intLogDao: IntLogDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allMessages: LiveData<List<IntDataBaseEntity>> = intLogDao.getAll()

    suspend fun insert(intDataBaseEntity: IntDataBaseEntity) {
        intLogDao.insert(intDataBaseEntity)
    }

    suspend fun deleteAll() {
        intLogDao.deleteAll()
    }
}