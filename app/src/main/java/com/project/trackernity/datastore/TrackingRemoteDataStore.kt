package com.project.trackernity.datastore

import com.project.trackernity.data.TrackernityApi
import com.project.trackernity.data.model.TrackernityResponseSecond
import com.project.trackernity.data.model.TrackernityResponseSecondItem
import timber.log.Timber

class TrackingRemoteDataStore (private val api:TrackernityApi) :TrackingDataStore {
    override suspend fun getData(param: String): MutableList<TrackernityResponseSecondItem>? {
        val response = api.getData(param)
        Timber.d("API Data: ${response.body()}")
        if (response.isSuccessful) return response.body()
        throw Exception("Terjadi kesalahan saat melakukan request data, status error ${response.code()}")
    }

    override suspend fun getData(
        param: String,
        param2: String,
        param3: String,
        param4: String
    ): MutableList<TrackernityResponseSecondItem>? {
        val response = api.getData(param,param2,param3,param4)
        Timber.d("API Data: ${response.body()}")
        if (response.isSuccessful) return response.body()
        throw Exception("Terjadi kesalahan saat melakukan request data, status error ${response.code()}")
    }

    override suspend fun addData(tracking: MutableList<TrackernityResponseSecondItem>?) {
    }
}