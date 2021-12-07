package com.stho.isee.ui.details

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.stho.isee.core.Entry

class DetailsViewModelFactory(
    private val application: Application,
    private val entry: Entry,
) : ViewModelProvider.AndroidViewModelFactory(application) {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailsViewModel::class.java)) {
            return DetailsViewModel(application = application, entry) as T
        }
        return super.create(modelClass)
    }
}