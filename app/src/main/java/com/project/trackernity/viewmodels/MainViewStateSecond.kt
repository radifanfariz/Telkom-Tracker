package com.project.trackernity.viewmodels

import com.project.trackernity.data.model.TrackernityResponse

data class MainViewStateSecond (
    val loading:Boolean = false,
    val error: String? = null,
    val data:TrackernityResponse? = null
    )