package com.project.trackernity.other

import androidx.lifecycle.*
import com.google.android.gms.maps.MapView

class MapLifecycleObserver(
    private val mapView: MapView?,
    lifecycle: Lifecycle
    //myLifecycleOwner: LifecycleOwner
) : DefaultLifecycleObserver {

    init {
        lifecycle.addObserver(this)
    }


//    init {
//        lifecycle.addObserver(this)
//    }

//    @OnLifecycleEvent(Lifecycle.Event.ON_START)
//    fun onStart() {
//        mapView?.onStart()
//    }
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
//    fun onResume() {
//        mapView?.onResume()
//    }
//
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
//    fun onPause() {
//        mapView?.onPause()
//    }
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
//    fun onStop() {
//        mapView?.onStop()
//    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        mapView?.onStart()
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        mapView?.onResume()
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        mapView?.onPause()
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        mapView?.onStop()
    }


}
