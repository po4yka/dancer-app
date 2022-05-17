package com.po4yka.dancer.ui.root

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RootViewModel
    @Inject
    constructor() : ViewModel() {
        init {
            Timber.d("init RootViewModel")
        }
    }
