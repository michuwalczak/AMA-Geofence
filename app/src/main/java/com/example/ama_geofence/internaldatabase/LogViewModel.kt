package com.example.ama_geofence.internaldatabase

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LogViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: LogRepository

    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allMessages: LiveData<List<IntDataBaseEntity>>

    init {
        val wordsDao = IntDataBase.getDatabase(application, viewModelScope).localMessageDao()
        repository =
            LogRepository(
                wordsDao
            )
        allMessages = repository.allMessages
    }

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(intDataBaseEntity: IntDataBaseEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(intDataBaseEntity)
    }

    fun deleteAll() = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteAll()
    }
}