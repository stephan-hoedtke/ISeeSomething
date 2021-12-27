package com.stho.isee.core

import android.content.ClipDescription
import android.content.Context
import android.media.audiofx.AudioEffect
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.stho.isee.encryption.Encryptor
import com.stho.isee.storage.Storage
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Default
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs

class Repository private constructor(context: Context) {

    private val persister = EntryPersister(storage = Storage(context), encryptor = Encryptor(context))

    private val entriesLiveData: MutableLiveData<ArrayList<Entry>> = MutableLiveData<ArrayList<Entry>>().apply {
        value = ArrayList()
        postValue(readEntriesOrFake())
    }

    private fun readEntriesOrFake(): ArrayList<Entry> {
        // TODO: remove fakes
        val list = persister.read()
        if (list.isEmpty()) {
            list.add(createFakeEntry(1))
            list.add(createFakeEntry(2))
            list.add(createFakeEntry(3))
        }
        return list
    }

    private val showFabLiveData: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { value = true }

    val entriesLD: LiveData<List<Entry>>
        get() = Transformations.map(entriesLiveData) { entries -> entries }

    val showFabLD: LiveData<Boolean>
        get() = showFabLiveData

    private val entries: ArrayList<Entry>
        get() = entriesLiveData.value ?: ArrayList()

    fun getEntry(id: Long): Entry =
        entries.first { entry -> entry.id == id }

    private fun createFakeEntry(id: Int): Entry =
        when (id) {
            1 -> createFakeEntry(
                id = -1,
                category = "Telephone",
                title = "FritzBox",
                url = "http:://fritz.box",
                description = "login at: fritz.box with username admin",
            )
            2 -> createFakeEntry(
                id = -2,
                category = "Computer",
                title = "DLink Storage",
                url = "192.168.178.200",
                description = "login with admin or with hoedtke at IP 192.168.178.200",
            )
            3 -> createFakeEntry(
                id = -3,
                category = "Computer",
                title = "Gitlab",
                url = "gitlab.com",
                description = "stephan hoedtke @ gitlab for Android projects ...",
            )
            else -> throw Exception("Invalid entry id")
        }

    private fun createFakeEntry(id: Long, category: String, title: String, url: String, description: String) =
        Entry.createNew(
            category = category,
            title = title,
            url = url,
            user = "admin",
            password = "something",
            description = description,
        ).also {
            it.setStatus(Entry.Status.NEW)
            it.setId(0 - abs(id))
        }

    fun showFab() {
        showFabLiveData.postValue(true)
    }

    fun hideFab() {
        showFabLiveData.postValue(false)
    }

    fun save(entry: Entry) {
        persister.save(entry)
        updateEntriesForSave(entry)
    }

    fun delete(entry: Entry) {
        persister.delete(entry)
        updateEntriesForDelete(entry)
    }

    private fun updateEntriesForSave(entry: Entry) {
        entries.also {
            if (!it.contains(entry)) {
                it.add(entry)
            }
            entriesLiveData.postValue(it)
        }
    }

    private fun updateEntriesForDelete(entry: Entry) {
        entries.also {
            if (it.contains(entry)) {
                it.remove(entry)
            }
            entriesLiveData.postValue(it)
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

