package com.project.trackernity.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.project.trackernity.R
import com.project.trackernity.data.model.TrackernityRequest
import com.project.trackernity.other.Constants.ACTION_FINISH_RUN
import com.project.trackernity.other.Constants.ACTION_PAUSE_SERVICE
import com.project.trackernity.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.project.trackernity.other.Constants.ACTION_START_SERVICE
import com.project.trackernity.other.Constants.ACTION_STOP_SERVICE
import com.project.trackernity.other.Constants.FASTEST_LOCATION_INTERVAL
import com.project.trackernity.other.Constants.LOCATION_UPDATE_INTERVAL
import com.project.trackernity.other.Constants.NOTIFICATION_CHANNEL_ID
import com.project.trackernity.other.Constants.NOTIFICATION_CHANNEL_NAME
import com.project.trackernity.other.Constants.NOTIFICATION_CHANNEL_TARGET_ID
import com.project.trackernity.other.Constants.NOTIFICATION_CHANNEL_TARGET_NAME
import com.project.trackernity.other.Constants.NOTIFICATION_ID
import com.project.trackernity.other.Constants.NOTIFICATION_TARGET_ID
import com.project.trackernity.other.Constants.REQUEST_CODE_ACTION_FINISH_RUN
import com.project.trackernity.other.TrackingUtility
import com.project.trackernity.repositories.TrackingRepository
import com.project.trackernity.repositories.TrackingRepository.Companion.isTracking
import com.project.trackernity.repositories.TrackingRepository.Companion.pathPoints
import com.project.trackernity.ui.MainActivity
import com.project.trackernity.util.DispatcherProvider
import com.project.trackernity.util.Resource
import com.project.trackernity.viewmodels.TrackingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class TrackingService: LifecycleService() {

    @Inject
    lateinit var trackingRepository: TrackingRepository
    private var isAlreadyNotifiedAboutTargetReached = false

    @Inject
    lateinit var locationRequest:LocationRequest

/////not used////
//    companion object {
//        lateinit var viewModelForService: TrackingViewModel
//    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            if (isTracking.value!!) {
                locationResult.locations.forEach(::addPathPoint)
            }
        }
    }

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @Inject
    @Named("baseNotificationBuilder")
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    @Inject
    @Named("targetReachedNotificationBuilder")
    lateinit var targetReachedNotificationBuilder: NotificationCompat.Builder

    override fun onCreate() {
        super.onCreate()
        trackingRepository.initStartingValues()

        isTracking.observe(this, Observer {

            updateLocationTracking(it)
            updateNotificationTrackingState(it)
        })

        TrackingRepository.isTargetReached.observe(this, Observer {
            if (it && !isAlreadyNotifiedAboutTargetReached) notifyTargetReached()
        })
    }


    // Handled by EasyPermissions
    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean) {
        if (isTracking) {
            if (TrackingUtility.hasLocationPermissions(this)) {
                locationRequest = LocationRequest.create().apply {
                    interval = LOCATION_UPDATE_INTERVAL
                    fastestInterval = FASTEST_LOCATION_INTERVAL
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                }
                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    private fun addPathPoint(location: Location?) {
        location?.let {
            trackingRepository.addPoint(LatLng(it.latitude, it.longitude))
//            viewModelForService.sendingData(location)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    Timber.d("Started or resumed service, first run: ${trackingRepository.isFirstRun}")
                    if (trackingRepository.isFirstRun) {
                        startForegroundService()
                        trackingRepository.startRunWithoutTimer(true)
                    } else {
                        Timber.d("Resuming service...")
                        trackingRepository.startRunWithoutTimer()
                    }
                }
                ACTION_START_SERVICE -> {
                    Timber.d("Paused service")
                    startForegroundService()
                    trackingRepository.startRunWithoutTimer(true)
                }
                ACTION_PAUSE_SERVICE -> {
                    Timber.d("Paused service")
                    trackingRepository.pauseRun()
                }
                ACTION_STOP_SERVICE -> {
                    Timber.d("Stopped service")
                    killService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    @SuppressLint("RestrictedApi")
    private fun updateNotificationTrackingState(isTracking: Boolean) {
        val notificationActionText =
            if (isTracking) getString(R.string.stop)
            else getString(R.string.resume)

        val pendingIntent = PendingIntent.getService(
            this,
            if (isTracking) 1 else 2,
            Intent(this, TrackingService::class.java).apply {
                action = if (isTracking) ACTION_STOP_SERVICE else ACTION_START_SERVICE
            },
            FLAG_UPDATE_CURRENT
        )

        baseNotificationBuilder.apply {
            mActions.clear()
            addAction(R.drawable.ic_baseline_pause_24, notificationActionText, pendingIntent)
        }

        if (!trackingRepository.isCancelled) {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(NOTIFICATION_ID, baseNotificationBuilder.build())
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun startForegroundService() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        startForeground(NOTIFICATION_ID, baseNotificationBuilder.build())

        TrackingRepository.isError.observe(this,{
            if (it) Toast.makeText(this,"Error Server 404 !",Toast.LENGTH_SHORT).show()
        })

        TrackingRepository.distanceInMeters.observe(this, Observer {
            if (!trackingRepository.isCancelled) {
                val distance = "${it} m"

                baseNotificationBuilder
                    .setContentTitle("Tracking Location")
                    .setContentText(distance)
                notificationManager.notify(NOTIFICATION_ID, baseNotificationBuilder.build())
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createTargetReachedNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_TARGET_ID,
            NOTIFICATION_CHANNEL_TARGET_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
    }

    @SuppressLint("RestrictedApi", "ObsoleteSdkInt")
    private fun notifyTargetReached() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createTargetReachedNotificationChannel(notificationManager)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            REQUEST_CODE_ACTION_FINISH_RUN,
            Intent(this, MainActivity::class.java).apply {
                action = ACTION_FINISH_RUN
            },
            FLAG_UPDATE_CURRENT
        )

        targetReachedNotificationBuilder
            .addAction(
                R.drawable.ic_baseline_pause_24,
                getString(R.string.finish),
                pendingIntent
            )

        if (!trackingRepository.isCancelled) {
            notificationManager.notify(
                NOTIFICATION_TARGET_ID,
                targetReachedNotificationBuilder.build()
            )
            isAlreadyNotifiedAboutTargetReached = true
        }
    }

    private fun killService() {
        trackingRepository.cancelRun()
        stopForeground(true)
        stopSelf()
        isAlreadyNotifiedAboutTargetReached = false
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }
}