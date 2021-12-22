package com.stho.isee.ui.details

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.stho.isee.core.Entry
import com.stho.isee.core.Repository

class DetailsViewModel(application: Application, entry: Entry) : AndroidViewModel(application) {

    enum class PasswordMode {
        VISIBLE,
        HIDDEN,
        HINTS,
        INPUT,
        EDIT,
    }

    private val repository = Repository.getInstance(application.applicationContext)
    private val backup: Entry = entry.clone()
    private val entryLiveData: MutableLiveData<Entry> = MutableLiveData<Entry>().apply { value = entry }
    private val passwordModeLiveData: MutableLiveData<PasswordMode> = MutableLiveData<PasswordMode>().apply { value = PasswordMode.HIDDEN }
    private val passwordMirrorLiveData: MutableLiveData<String> = MutableLiveData<String>().apply { value = entry.password }

    val statusLD: LiveData<Entry.Status>
        get() = Transformations.map(entryLD) { entry -> entry.status }

    val entryLD: LiveData<Entry>
        get() = entryLiveData

    val passwordModeLD: LiveData<PasswordMode>
        get() = passwordModeLiveData

    val passwordMirrorLD: LiveData<String>
        get() = passwordMirrorLiveData

    var passwordMirror: String
        get() = passwordMirrorLiveData.value ?: ""
        set(value) {
            if (passwordMirrorLiveData.value != value) {
                passwordMirrorLiveData.postValue(value)
            }
        }

    val isModified: Boolean
        get() {
            entryLiveData.value?.let {
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

    var passwordMode: PasswordMode
        get() = passwordModeLiveData.value ?: PasswordMode.HIDDEN
        set(value) {
            if (passwordModeLiveData.value != value) {
                passwordModeLiveData.postValue(value)
            }
        }

    var plainText: Boolean
        get() = isPasswordVisible(passwordMode)
        set(value) {
            if (value) {
                when (passwordMode) {
                    PasswordMode.HIDDEN -> passwordMode = PasswordMode.VISIBLE
                    PasswordMode.INPUT -> passwordMode = PasswordMode.EDIT
                    else -> {} // just to make Lint happy
                }
            } else {
                when (passwordMode) {
                    PasswordMode.EDIT -> passwordMode = PasswordMode.INPUT
                    PasswordMode.VISIBLE -> passwordMode = PasswordMode.HIDDEN
                    else -> {} // just to make Lint happy
                }
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

    companion object {
        fun isPasswordVisible(mode: PasswordMode): Boolean {
            return when (mode) {
                PasswordMode.VISIBLE -> true
                PasswordMode.HIDDEN -> false
                PasswordMode.HINTS -> false
                PasswordMode.INPUT -> false
                PasswordMode.EDIT -> true
            }
        }
    }
}
