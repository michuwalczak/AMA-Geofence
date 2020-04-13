package com.example.ama_geofence.internaldatabase

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface IntLogDao {
    @Query("SELECT * FROM intDatabaseEntity")
    fun getAll(): LiveData<List<IntDataBaseEntity>>

    @Query("SELECT * FROM intDatabaseEntity WHERE id IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<IntDataBaseEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(intDataBaseEntity: IntDataBaseEntity)

    @Query("DELETE FROM IntDataBaseEntity")
    suspend fun deleteAll()

    @Delete
    fun delete(intDataBaseEntity: IntDataBaseEntity)
}