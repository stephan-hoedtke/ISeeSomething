package com.stho.isee.ui.list

import android.app.Application
import androidx.lifecycle.*
import com.stho.isee.core.Entry
import com.stho.isee.core.Repository

class ListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = Repository.getInstance(application.applicationContext)

    private val categoryLiveData: MutableLiveData<String> = MutableLiveData<String>()

    val categoryLD: LiveData<String>
        get() = categoryLiveData

    val entriesLD
        get() = Transformations.switchMap(categoryLiveData) {
            category -> Transformations.map(repository.entriesLD) {
                entries -> filterEntries(entries, category)
            }
        }

    fun setCategory(category: String) {
        if (categoryLiveData.value != category) {
            categoryLiveData.value = category
        }
    }

    fun showFab() {
        repository.showFab()
    }

    private fun filterEntries(entries: List<Entry>, category: String): List<Entry> =
        entries.filter { entry -> entry.category == category }

}


