package com.stho.isee.ui.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.stho.isee.core.Entry
import com.stho.isee.core.Repository

class ListViewModel(application: Application, private val category: String) : AndroidViewModel(application) {

    private val repository = Repository.getInstance(application.applicationContext)

    private val categoryLiveData: MutableLiveData<String> = MutableLiveData<String>().apply { value = category }

    val categoryLD: LiveData<String>
        get() = categoryLiveData

    val entriesLD
        get() = Transformations.map(repository.entriesLD) { entries -> getEntriesFor(entries, category) }

    private fun getEntriesFor(entries: List<Entry>, category: String): List<Entry> {
        return entries.filter { entry -> entry.category == category }
    }
}


