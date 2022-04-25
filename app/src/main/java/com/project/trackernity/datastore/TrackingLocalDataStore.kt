package com.project.trackernity.datastore

import com.project.trackernity.data.model.TrackernityResponseSecond
import com.project.trackernity.data.model.TrackernityResponseSecondItem

class TrackingLocalDataStore:TrackingDataStore {
    private var caches = mutableListOf<TrackernityResponseSecondItem>()

    override suspend fun getData(param: String): MutableList<TrackernityResponseSecondItem>? =
        if (caches.isNotEmpty()) caches else null

    override suspend fun getData(
        param: String,
        param2: String,
        param3: String,
        param4: String
    ): MutableList<TrackernityResponseSecondItem>? =
        if (caches.isNotEmpty()) caches else null


    override suspend fun addData(tracking: MutableList<TrackernityResponseSecondItem>?) {
        tracking?.let { caches = it }
    }
}