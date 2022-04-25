package com.project.trackernity.repositories

import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.project.trackernity.data.TrackernityApi
import com.project.trackernity.data.model.TrackernityRequest
import com.project.trackernity.data.model.TrackernityRequestSecond
import com.project.trackernity.data.model.TrackernityRequestThird
import com.project.trackernity.data.model.TrackernityResponse
import com.project.trackernity.util.Resource
import okhttp3.RequestBody
import javax.inject.Inject

class DefaultMainRepository @Inject constructor(
    private val api: TrackernityApi
): MainRepository {

    override suspend fun sendData(trackernityRequest: TrackernityRequest): Resource<TrackernityResponse> {
        return try {
            val response = api.sendData(trackernityRequest)
            val result = response.body()
            if(response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error(response.message())
            }
        }catch (e: Exception){
            Resource.Error(e.message ?: "An error occured")
        }
    }

    override suspend fun sendDataSecond(trackernityRequestSecond: TrackernityRequestSecond): Resource<TrackernityResponse> {
        return try{
            val response = api.sendDataSecond(trackernityRequestSecond)
            val result  = response.body()
            if (response.isSuccessful && result != null){
                Resource.Success(result)
            }else{
                Resource.Error(response.message())
            }
        }catch (e:Exception){
            Resource.Error(e.message ?: "An error occured")
        }
    }

    override suspend fun sendDataThird(trackernityRequestThird: TrackernityRequestThird): Resource<TrackernityResponse> {
        return try{
            val response = api.sendDataThird(trackernityRequestThird)
            val result  = response.body()
            if (response.isSuccessful && result != null){
                Resource.Success(result)
            }else{
                Resource.Error(response.message())
            }
        }catch (e:Exception){
            Resource.Error(e.message ?: "An error occured")
        }
    }

    companion object{
        val userId = MutableLiveData("")
        val latLngTriggered = MutableLiveData<LatLng>()
//        val polyLineTriggered = MutableLiveData<Polyline>()
    }
}