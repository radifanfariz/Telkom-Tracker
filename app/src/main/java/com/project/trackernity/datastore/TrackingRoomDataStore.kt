package com.project.trackernity.datastore

import com.project.trackernity.data.model.TrackernityResponseSecond
import com.project.trackernity.data.model.TrackernityResponseSecondItem
import com.project.trackernity.db.TrackingDAO
import timber.log.Timber

class TrackingRoomDataStore(private val trackingDao:TrackingDAO):TrackingDataStore {
    override suspend fun getData(param: String): MutableList<TrackernityResponseSecondItem>? {
        try {
            val response = trackingDao.getAllTrackingsFilteredByRemarks(param)
            Timber.d("Room Data: ${response}")
            return if (response.isNotEmpty()) response else null
        }catch (ex:Exception){
            Timber.d("error Room: ${ex}")
            return null
        }

    }

    override suspend fun getData(
        param: String,
        param2: String,
        param3: String,
        param4: String
    ): MutableList<TrackernityResponseSecondItem>? {
        try {
            val response = trackingDao.getAllTrackingsFilteredByRemarks(param)
            Timber.d("Room Data: ${response}")
            return if (response.isNotEmpty()) response else null
        }catch (ex:Exception){
            Timber.d("error Room: ${ex}")
            return null
        }
    }

    override suspend fun addData(tracking: MutableList<TrackernityResponseSecondItem>?) {
        tracking?.let {
            try {
                trackingDao.insertTracking(*it.toTypedArray())
            }catch (ex:Exception){
                Timber.d("error Room: ${ex}")
            }

        }
    }
}