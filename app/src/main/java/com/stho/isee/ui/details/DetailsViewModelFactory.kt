package com.stho.isee.ui.details

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.stho.isee.core.Entry
import java.lang.Exception

class DetailsViewModelFactory(
    private val application: Application,
    private val entry: Entry,
) : ViewModelProvider.AndroidViewModelFactory(application) {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        when {
            modelClass.isAssignableFrom(EditDetailsViewModel::class.java) -> {
                EditDetailsViewModel(application = application, entry) as T
            }
            modelClass.isAssignableFrom(ViewDetailsViewModel::class.java) -> {
                ViewDetailsViewModel(application = application, entry) as T
            }
            else -> {
                throw Exception("Invalid class requested from DetailsViewModelFactory: ${modelClass.name}")
            }
        }
}

