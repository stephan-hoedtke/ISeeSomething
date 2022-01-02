package com.stho.isee.ui.details

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.stho.isee.core.Entry
import com.stho.isee.core.Repository

class ViewDetailsViewModel(application: Application, entry: Entry) : AndroidViewModel(application) {

    enum class PasswordMode {
        VISIBLE,
        HIDDEN,
        HINTS,
    }

    private val repository = Repository.getInstance(application.applicationContext)
    private val entryLiveData: MutableLiveData<Entry> = MutableLiveData<Entry>().apply { value = entry }
    private val passwordModeLiveData: MutableLiveData<PasswordMode> = MutableLiveData<PasswordMode>().apply { value = PasswordMode.HIDDEN }
    private val passwordMirrorLiveData: MutableLiveData<String> = MutableLiveData<String>().apply { value = entry.password }

    val entryLD: LiveData<Entry>
        get() = entryLiveData

    val statusLD: LiveData<Entry.Status>
        get() = Transformations.map(entryLD) { entry -> entry.status }

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

    val entry: Entry
        get() = entryLiveData.value!!

    val passwordLength: Int
        get() = entry.password.length

    var passwordMode: PasswordMode
        get() = passwordModeLiveData.value ?: PasswordMode.HIDDEN
        set(value) {
            // set the value always unconditionally to force all relevant items to be refreshed
            passwordModeLiveData.postValue(value)
        }

    fun hideFab() {
        repository.hideFab()
    }

    companion object {
        fun isPasswordVisible(mode: PasswordMode): Boolean {
            return when (mode) {
                PasswordMode.VISIBLE -> true
                PasswordMode.HIDDEN -> false
                PasswordMode.HINTS -> false
            }
        }
    }
}
