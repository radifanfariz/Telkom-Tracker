package com.project.trackernity.repositories

import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.project.trackernity.data.model.TrackernityResponseSecondItem
import com.project.trackernity.datastore.TrackingDataStore
import com.project.trackernity.datastore.TrackingLocalDataStore
import com.project.trackernity.datastore.TrackingRemoteDataStore
import com.project.trackernity.db.TrackingDAO
import javax.inject.Inject

class ListRepository constructor(
    private val localDataStore: TrackingDataStore,
    private val remoteDataStore: TrackingDataStore
) {
    suspend fun getData(param:String): MutableList<TrackernityResponseSecondItem>?{
        val caches = localDataStore.getData(param)
        if (caches != null) return caches
        val response = remoteDataStore.getData(param)
        return response
    }

    suspend fun getData(param:String,param2: String,param3: String, param4: String): MutableList<TrackernityResponseSecondItem>?{
        val caches = localDataStore.getData(param,param2,param3,param4)
        if (caches != null) return caches
        val response = remoteDataStore.getData(param,param2,param3,param4)
        return response
    }

//    ///////////////for recyclerview listener////////////
//    companion object{
//        val latLngTriggered = MutableLiveData<LatLng>()
//    }

}