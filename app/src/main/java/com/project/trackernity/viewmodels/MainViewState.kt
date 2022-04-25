package com.project.trackernity.viewmodels

import com.project.trackernity.data.model.TrackernityResponseSecondItem

data class MainViewState (
    val loading:Boolean = false,
    val error: Exception? = null,
    val data:MutableList<TrackernityResponseSecondItem>? = null
    )