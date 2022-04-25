package com.project.trackernity.repositories

import com.project.trackernity.data.model.TrackernityResponseSecondItem
import com.project.trackernity.datastore.TrackingDataStore

class ListThirdRepository constructor(
    private val localDataStore: TrackingDataStore,
    private val remoteDataStore: TrackingDataStore
)  {

    suspend fun getDataThird(param: String): MutableList<TrackernityResponseSecondItem>? {
        val caches = localDataStore.getData(param)
        if (caches != null) return caches
        return remoteDataStore.getData(param)
    }

//    ///////////////for recyclerview listener////////////
//    companion object{
//        val latLngTriggered = MutableLiveData<LatLng>()
//    }
}