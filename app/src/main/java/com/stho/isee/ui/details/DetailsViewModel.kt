package com.stho.isee.ui.details

import android.app.Application
import android.text.Editable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.stho.isee.core.Entry
import com.stho.isee.core.Repository

class DetailsViewModel(application: Application, entry: Entry) : AndroidViewModel(application) {

    enum class PasswordMode {
        HIDDEN,
        HINTS,
        EDIT,
    }
    private val repository = Repository.getInstance(application.applicationContext)
    private val backup: Entry = entry.clone()
    private val entryLiveData: MutableLiveData<Entry> = MutableLiveData<Entry>().apply { value = entry }
    private val passwordModeLiveData: MutableLiveData<PasswordMode> = MutableLiveData<PasswordMode>().apply { value = PasswordMode.HIDDEN }

    val statusLD: LiveData<Entry.Status>
        get() = Transformations.map(entryLD) { entry -> entry.status }

    val entryLD: LiveData<Entry>
        get() = entryLiveData

    val passwordModeLD: LiveData<PasswordMode>
        get() = passwordModeLiveData

    val isModified: Boolean
        get() = entryLiveData.value?.isModified ?: false

    val entry: Entry
        get() = entryLiveData.value!!

    val passwordLength: Int
        get() = entry.password.length

    fun getPasswordHint(index: Int): String {
        val password = entry.password
        val length = password.length
        val hint = (0 until length).map { i -> if (i == index) password[i] else '*' }
        return hint.joinToString("")
    }

    val passwordMode: PasswordMode
        get() = passwordModeLiveData.value ?: PasswordMode.HIDDEN

    fun setPasswordMode(newMode: PasswordMode) {
        if (passwordModeLiveData.value != newMode) {
            passwordModeLiveData.postValue(newMode)
        }
    }

    fun touch() {
        entryLiveData.postValue(entryLiveData.value)
    }

    fun save() {
        // TODO implement real save...
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

    fun setPassword(newPassword: String) {
        entryLiveData.value?.also {
            if (it.password != newPassword) {
                it.password = newPassword
                entryLiveData.postValue(it)
            }
        }
    }
}

