package com.project.trackernity.repositories

import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.project.trackernity.data.model.TrackernityResponseSecondItem
import com.project.trackernity.datastore.TrackingDataStore

class ListSecondRepository constructor(
    private val localDataStore: TrackingDataStore,
    private val remoteDataStore: TrackingDataStore
)  {

    suspend fun getDataSecond(param:String): MutableList<TrackernityResponseSecondItem>?{
        val caches = localDataStore.getData(param)
        if (caches != null) return caches
        val response = remoteDataStore.getData(param)
        return response
    }

//    ///////////////for recyclerview listener////////////
//    companion object{
//        val latLngTriggered = MutableLiveData<LatLng>()
//    }
}