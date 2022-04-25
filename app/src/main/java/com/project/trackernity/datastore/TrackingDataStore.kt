package com.project.trackernity.datastore

import com.project.trackernity.data.model.TrackernityResponseSecond
import com.project.trackernity.data.model.TrackernityResponseSecondItem

interface TrackingDataStore {
    suspend fun getData(param:String):MutableList<TrackernityResponseSecondItem>?
    suspend fun getData(param:String,param2:String,param3:String,param4: String):MutableList<TrackernityResponseSecondItem>?
    suspend fun addData(tracking:MutableList<TrackernityResponseSecondItem>?)
}