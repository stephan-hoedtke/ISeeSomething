package com.stho.isee

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.stho.isee.core.Repository

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository =
        Repository.getInstance(application.applicationContext)

    val showFabLD: LiveData<Boolean> =
        repository.showFabLD
}
