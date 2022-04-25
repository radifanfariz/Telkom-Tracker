package com.project.trackernity.viewmodels

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.trackernity.data.model.TrackernityRequest
import com.project.trackernity.other.Constants.ACTION_PAUSE_SERVICE
import com.project.trackernity.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.project.trackernity.other.Constants.ACTION_START_SERVICE
import com.project.trackernity.other.Constants.ACTION_STOP_SERVICE
import com.project.trackernity.repositories.DefaultMainRepository
import com.project.trackernity.repositories.Polylines
import com.project.trackernity.repositories.TrackingRepository
import com.project.trackernity.services.TrackingService
import com.project.trackernity.util.DispatcherProvider
import com.project.trackernity.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception
import java.util.*
import javax.inject.Inject
import kotlin.math.round

class TrackingViewModel : ViewModel(){

    val isTracking = TrackingRepository.isTracking
    val pathPoints = TrackingRepository.pathPoints
    val currentTimeInMillis = TrackingRepository.timeRunInMillis
    val distanceInMeters = TrackingRepository.distanceInMeters
    val remarks = TrackingRepository.remarks
    val token = TrackingRepository.token
    val errorTrackingMsg = TrackingRepository.errorTrackingMsg
//    val userData = TrackingRepository.userData

//    fun sendCommandToService(context: Context) {
//        val action =
//                if (isTracking.value!!) ACTION_PAUSE_SERVICE
//                else ACTION_START_OR_RESUME_SERVICE
//        Intent(context, TrackingService::class.java).also {
//            it.action = action
//            context.startService(it)
//        }
//    }

    fun setStartCommandService(context: Context) {
        Intent(context, TrackingService::class.java).also {
            it.action = ACTION_START_SERVICE
            context.startService(it)
        }
    }

    fun setStopCommandService(context: Context) {
        Intent(context, TrackingService::class.java).also {
            it.action = ACTION_STOP_SERVICE
            context.startService(it)
        }
    }

}