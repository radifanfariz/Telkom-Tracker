package com.project.trackernity.repositories

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.project.trackernity.data.TrackernityApi
import com.project.trackernity.data.model.TrackernityRequest
import com.project.trackernity.data.model.TrackernityResponse
import com.project.trackernity.other.Constants
import com.project.trackernity.other.TargetType
import com.project.trackernity.ui.fragments.ui.login.LoggedInUserView
import com.project.trackernity.ui.fragments.ui.login.LoginResultToNavigate
import com.project.trackernity.util.DispatcherProvider
import com.project.trackernity.util.Resource
import com.project.trackernity.viewmodels.TrackingViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.RequestBody
import org.json.JSONObject
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

typealias Polyline = MutableList<LatLng>
typealias Polylines = MutableList<Polyline>

class TrackingRepository @Inject constructor(
    private val repository: DefaultMainRepository,
    private val dispatchers: DispatcherProvider
) {


    var isFirstRun = true
        private set
    var isCancelled = false
    private var timeStarted = 0L
    private var timeRun = 0L
    private var lapTime = 0L
    private var lastSecondTimestamp = 0L
    private var lastCounted = 0

    private val mViewState =  MutableLiveData<SendingEvent>().apply {
        value = SendingEvent.Empty
    }

    val viewState: LiveData<SendingEvent>
        get() = mViewState

    fun initStartingValues() {
        pathPoints.value = mutableListOf()
    }

    private fun startTracking() {
        addEmptyPolyline()
        isTracking.value = true

    }

    fun startRunWithoutTimer(firstRun: Boolean = false){
        startTracking()
        isCancelled = false
        isFirstRun = firstRun

        CoroutineScope(Dispatchers.Main).launch {
            try {
                while (isTracking.value!!) {
                    val lastPointIndex = pathPoints.value!!.last().lastIndex
                    if (lastPointIndex > lastCounted) {
                        var distance = 0f
                        for (i in lastCounted until lastPointIndex) {
                            val pos1 = pathPoints.value!!.last()[i]
                            val pos2 = pathPoints.value!!.last()[i + 1]


                            sendingData(pos2)

                            val result = FloatArray(1)
                            Location.distanceBetween(
                                pos1.latitude,
                                pos1.longitude,
                                pos2.latitude,
                                pos2.longitude,
                                result
                            )
                            distance += result[0]
                        }
                        lastCounted = lastPointIndex
                        val newDistance = distanceInMeters.value!! + distance.toInt()
                        distanceInMeters.postValue(newDistance)
                    }
                    delay(Constants.UPDATE_INTERVAL)
                }
            }catch (ex: Exception){
                errorTrackingMsg.value = "Tracking error please re-login !"
            }
        }
    }

    fun startRun(firstRun: Boolean = false){
        startTracking()
        timeStarted = System.currentTimeMillis()
        isCancelled = false
        isFirstRun = firstRun

        CoroutineScope(Dispatchers.Main).launch {
            while (isTracking.value!!){
                //val progressValue = distanceInMeters.value!!.toFloat()
//                lapTime = System.currentTimeMillis() - timeStarted
//                timeRunInMillis.postValue(timeRun + lapTime)

                if (timeRunInMillis.value!! >= lastSecondTimestamp + 1000L){
                    val lastPointIndex = pathPoints.value!!.last().lastIndex
                    if(lastPointIndex > lastCounted){
                        var distance = 0f
                        for (i in lastCounted until lastPointIndex){
                            val pos1 = pathPoints.value!!.last()[i]
                            val pos2 = pathPoints.value!!.last()[i + 1]

                            val result = FloatArray(1)
                            Location.distanceBetween(
                                pos1.latitude,
                                pos1.longitude,
                                pos2.latitude,
                                pos2.longitude,
                                result
                            )
                            distance += result[0]
                        }
                        lastCounted = lastPointIndex
                        val newDistance = distanceInMeters.value!! + distance.toInt()
                        distanceInMeters.postValue(newDistance)
                    }
                    timeRunInSeconds.postValue(timeRunInSeconds.value!! + 1)
                    lastSecondTimestamp += 1000L
                }
                delay(Constants.UPDATE_INTERVAL)
            }
            timeRun += lapTime
        }
    }

    fun pauseRun() {
        isTracking.value = false
        lastCounted = 0
    }

    fun cancelRun() {
        isCancelled = true
        isFirstRun = true
        timeRunInMillis.value = 0L // reset value for correct fragment observers income values
        distanceInMeters.value = 0
        caloriesBurned.value = 0
        progress.value = 0
        isTargetReached.value = false
        pauseRun()
        initStartingValues()
    }



    fun addPoint(latLng: LatLng) {
        pathPoints.value?.last()?.add(latLng)
        pathPoints.postValue(pathPoints.value)
    }

    private fun addEmptyPolyline() {
        pathPoints.value?.add(mutableListOf())
    }



    /////////////////////////Sending Data Background////////////////
    /////////////////////////Sending Data Background////////////////
    /////////////////////////Sending Data Background////////////////

    sealed class SendingEvent {
        class Success(val resultText: String): SendingEvent()
        class Failure(val errorText: String): SendingEvent()
        object Loading : SendingEvent()
        object Empty : SendingEvent()
    }

    private fun sendingData(latlng: LatLng){

       val userDataItem = userDataFromSharedPreference.value

        if (userDataItem != null) {

            val nama = userDataItem.nama
            val regional = userDataItem.regional
            val witel = userDataItem.witel
            val unit = userDataItem.unit
            val c_profile = userDataItem.c_profile
            val userId = userDataItem.userId
            val lat = latlng.latitude
            val lgt = latlng.longitude

            Timber.d("Data Check: ${regional}")

            val trackernityRequest = TrackernityRequest(
                nama = nama,
                regional = regional,
                witel = witel,
                unit = unit,
                c_profile = c_profile,
                user_id = userId,
                lat = lat,
                lgt =  lgt,
                continuity =  10,
                remarks =  remarks.value,
                token =  token.value
            )


            CoroutineScope(dispatchers.io).launch {
                try {

                    mViewState.postValue(SendingEvent.Loading)
                    when (val sendResponse = repository.sendData(trackernityRequest)) {
                        is Resource.Error -> {
                            mViewState.postValue(SendingEvent.Failure(sendResponse.message!!))
                            Timber.d("Error Sending Data!!!")
                            isError.postValue(true)
                        }
                        is Resource.Success -> {
                            Timber.d("Success Sending Data!!!")
                            val status = sendResponse.data!!.status
                            Timber.d("Status : ${status}")
                            mViewState.postValue(
                                SendingEvent.Success(
                                    status
                                )
                            )
                        }
                    }
                } catch (ex: Exception) {
                    Timber.d("Error Sending Data: ${ex}")
                    isError.postValue(true)
                }
            }
        }else{
            return
        }
    }

    companion object {
        val isTracking = MutableLiveData(false)
        val pathPoints = MutableLiveData<Polylines>()
        val timeRunInMillis = MutableLiveData(0L)
        val timeRunInSeconds = MutableLiveData(0L)
        val distanceInMeters = MutableLiveData(0)
        val caloriesBurned = MutableLiveData(0)
        val progress = MutableLiveData(0)
        //var targetType = MutableLiveData(TargetType.NONE)
        var isTargetReached = MutableLiveData(false)
        var isError = MutableLiveData(false)
        /////tracking send request item
        val remarks = MutableLiveData("")
        ////token for tracking/////
        val token = MutableLiveData("")
        ////userData from LoginFragment/////
        val userData = MutableLiveData<LoginResultToNavigate>()
        val userDataFromSharedPreference = MutableLiveData<LoginResultToNavigate>()
        ////error whle tracking/////////
        val errorTrackingMsg = MutableLiveData("")
    }

    /////////////////HTTP Request////////////////////////

//    fun jsonRequest(
//        userid: String,
//        latLang: LatLng,
//        continuity:Int,
//        remarks:String,
//    ){
//        val lat = latLang.latitude.toFloat()
//        val lgn = latLang.longitude.toFloat()
//        val trackernityRequest = TrackernityRequest(
//            userid,
//            lat,
//            lgn,
//            continuity,
//            remarks
//        )
//        val json = Json.encodeToString(TrackernityRequest.serializer(), trackernityRequest)
//        Timber.d(json)
//    }

//    override suspend fun getData(requestBody: RequestBody): Resource<TrackernityResponse> {
//        return try {
//            val response = api.getData(requestBody)
//            val result = response.body()
//            if(response.isSuccessful && result != null) {
//                Resource.Success(result)
//            } else {
//                Resource.Error(response.message())
//            }
//        }catch (e: Exception){
//            Resource.Error(e.message ?: "An error occured")
//        }
//    }
//
//    override suspend fun sendData(requestBody: RequestBody): Resource<TrackernityResponse> {
//        return try {
//            val response = api.sendData(requestBody)
//            val result = response.body()
//            if(response.isSuccessful && result != null) {
//                Resource.Success(result)
//            } else {
//                Resource.Error(response.message())
//            }
//        }catch (e: Exception){
//            Resource.Error(e.message ?: "An error occured")
//        }
//    }
}