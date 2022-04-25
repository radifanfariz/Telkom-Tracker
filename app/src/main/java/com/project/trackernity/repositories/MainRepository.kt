package com.project.trackernity.repositories

import com.project.trackernity.data.model.TrackernityRequest
import com.project.trackernity.data.model.TrackernityRequestSecond
import com.project.trackernity.data.model.TrackernityRequestThird
import com.project.trackernity.data.model.TrackernityResponse
import com.project.trackernity.util.Resource
import okhttp3.RequestBody

interface MainRepository {

    //suspend fun getData(requestBody: RequestBody): Resource<TrackernityResponse>
    suspend fun sendData(trackernityRequest: TrackernityRequest): Resource<TrackernityResponse>
    suspend fun sendDataSecond(trackernityRequestSecond: TrackernityRequestSecond):Resource<TrackernityResponse>
    suspend fun sendDataThird(trackernityRequestThird: TrackernityRequestThird):Resource<TrackernityResponse>
}