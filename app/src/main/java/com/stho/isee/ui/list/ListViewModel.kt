package com.stho.isee.ui.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.stho.isee.core.Entry
import com.stho.isee.core.Repository

class ListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = Repository.getInstance(application.applicationContext)

    private val categoryLiveData: MutableLiveData<String> = MutableLiveData<String>()

    val categoryLD: LiveData<String>
        get() = categoryLiveData

    val entriesLD
        get() = Transformations.map(repository.entriesLD) { entries -> entries.filter { entry -> entry.category == category } }

    fun setCategory(category: String) {
        if (categoryLiveData.value != category) {
            categoryLiveData.postValue(category)
        }
    }

    private val category: String
        get() = categoryLiveData.value ?: ""
}


