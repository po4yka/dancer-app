package com.po4yka.dancer.ui.root

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import timber.log.Timber

@HiltViewModel
class RootViewModel @Inject constructor() : ViewModel() {
    init {
        Timber.d("init RootViewModel")
    }
}
