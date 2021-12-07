package com.stho.isee.ui.details

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.stho.isee.core.Entry
import com.stho.isee.core.Repository

class DetailsViewModel(application: Application, entry: Entry) : AndroidViewModel(application) {

    private val repository = Repository.getInstance(application.applicationContext)

    private val entryLiveData: MutableLiveData<Entry> = MutableLiveData<Entry>().apply { value = entry }

    val entryLD: LiveData<Entry>
        get() = entryLiveData

    fun touch() {
        entryLiveData.postValue(entryLiveData.value)
    }
}

