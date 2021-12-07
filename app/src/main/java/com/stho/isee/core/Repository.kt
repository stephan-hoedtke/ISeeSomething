package com.stho.isee.core

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import java.util.*
import kotlin.collections.ArrayList

class Repository(context: Context) {

    private val entriesLiveData: MutableLiveData<ArrayList<Entry>> = MutableLiveData<ArrayList<Entry>>().apply {
        value = getEntriesFromDB(context)
    }

    val entriesLD: LiveData<List<Entry>>
        get() = Transformations.map(entriesLiveData) { entries -> entries }

    private fun getEntriesFromDB(context: Context): ArrayList<Entry> {
        return ArrayList<Entry>().also {
            it.add(getEntry(1))
            it.add(getEntry(2))
        }
    }

    fun getEntry(id: Int): Entry {
        // TODO: replace fake implementation
        return when (id) {
            1 -> Entry.createFromDB(
                id = 1,
                category = "Telephone",
                title = "FritzBox",
                url = "http:://fritz.box",
                user = "admin",
                password = "something",
                description = "login at: fritz.box with username admin",
                created = Calendar.getInstance(),
                modified = Calendar.getInstance(),
            )
            2 -> Entry.createFromDB(
                id = 2,
                category = "Computer",
                title = "DLink Storage",
                url = "192.168.178.200",
                user = "admin",
                password = "something",
                description = "login with admin or with hoedtke at IP 192.168.178.200",
                created = Calendar.getInstance(),
                modified = Calendar.getInstance(),
            )
            else -> throw Exception("Invalid entry id")
        }
    }

    companion object {

        fun  getInstance(context: Context): Repository {
            synchronized(lock) {
                return singleton ?: Repository(context).also {
                    singleton = it
                }
            }
        }

        private val lock = Object()
        private var singleton: Repository? = null
    }
}

