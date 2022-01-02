package com.stho.isee.ui.details

import android.accounts.AuthenticatorDescription
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.stho.isee.core.Entry
import com.stho.isee.core.Repository

class EditDetailsViewModel(application: Application, entry: Entry) : AndroidViewModel(application) {

    private val repository = Repository.getInstance(application.applicationContext)
    private val backup: Entry = entry.clone()
    private val entryLiveData: MutableLiveData<Entry> = MutableLiveData<Entry>().apply { value = entry }
    private val passwordMirrorLiveData: MutableLiveData<String> = MutableLiveData<String>().apply { value = entry.password }

    val entryLD: LiveData<Entry>
        get() = entryLiveData

    val statusLD: LiveData<Entry.Status>
        get() = Transformations.map(entryLD) { entry -> entry.status }

    val passwordMirrorLD: LiveData<String>
        get() = passwordMirrorLiveData

    var passwordMirror: String
        get() = passwordMirrorLiveData.value ?: ""
        set(value) {
            if (passwordMirrorLiveData.value != value) {
                passwordMirrorLiveData.postValue(value)
            }
        }

    val isDirty: Boolean
        get() {
            entryLiveData.value?.let {
                if (it.isNew)
                    return true
                if (it.isModified)
                    return true
                if (it.password != passwordMirror)
                    return true
            }
            return false
        }

    val entry: Entry
        get() = entryLiveData.value!!

    val passwordLength: Int
        get() = entry.password.length

    fun touch() {
        entryLiveData.postValue(entryLiveData.value)
    }

    fun save() {
        repository.save(entry)
    }

    fun restore() {

    }

    fun hideFab() {
        repository.hideFab()
    }

    fun setCategory(newCategory: String) {
        entryLiveData.value?.also {
            if (it.category != newCategory) {
                it.category = newCategory
                entryLiveData.postValue(it)
            }
        }
    }

    fun setTitle(newTitle: String) {
        entryLiveData.value?.also {
            if (it.title != newTitle) {
                it.title = newTitle
                entryLiveData.postValue(it)
            }
        }
    }

    fun setUser(newUser: String) {
        entryLiveData.value?.also {
            if (it.user != newUser) {
                it.user = newUser
                entryLiveData.postValue(it)
            }
        }
    }

    fun setUrl(newUrl: String) {
        entryLiveData.value?.also {
            if (it.url != newUrl) {
                it.url = newUrl
                entryLiveData.postValue(it)
            }
        }
    }

    fun setDescription(newDescription: String) {
        entryLiveData.value?.also {
            if (it.description != newDescription) {
                it.description = newDescription
                entryLiveData.postValue(it)
            }
        }
    }

    fun setPassword(newPassword: String) {
        entryLiveData.value?.also {
            if (it.password != newPassword) {
                it.password = newPassword
                entryLiveData.postValue(it)
            }
        }
    }
}
