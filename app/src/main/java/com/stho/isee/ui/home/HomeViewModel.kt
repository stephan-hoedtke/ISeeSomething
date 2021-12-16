package com.stho.isee.ui.home

import android.app.Application
import androidx.lifecycle.*
import com.stho.isee.core.Entry
import com.stho.isee.core.Repository

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = Repository.getInstance(application.applicationContext)

    val categoriesLD: LiveData<List<HomeListEntry>>
        get() = Transformations.map(repository.entriesLD) { entries -> group(entries) }

    companion object {

        private fun group(entries: Collection<Entry>): List<HomeListEntry> {
            val list = ArrayList<HomeListEntry>()
            val map = HashMap<String, Int>()
            for (entry in entries) {
                val category = entry.category
                val n: Int = map.getOrDefault(category, defaultValue = 0)
                map[category] = n + 1
            }
            for (p in map) {
                val category = p.key
                val n = p.value
                list.add(HomeListEntry(category, n))
            }
            return list
        }
    }

    fun showFab() {
        repository.showFab()
    }

}

