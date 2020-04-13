package com.example.ama_geofence.internaldatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [IntDataBaseEntity::class], version = 1, exportSchema = false)
abstract class IntDataBase : RoomDatabase() {
    abstract fun localMessageDao(): IntLogDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: IntDataBase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): IntDataBase {
            val tempInstance =
                INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    IntDataBase::class.java,
                    "localDataBase"
                )
                    .addCallback(
                        LocalDatabaseCallback(
                            scope
                        )
                    )
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }

    private class LocalDatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.localMessageDao())
                }
            }
        }

        suspend fun populateDatabase(intLogDao: IntLogDao) {
            // Delete all content here.
            //intLogDao.deleteAll()
        }
    }
}
