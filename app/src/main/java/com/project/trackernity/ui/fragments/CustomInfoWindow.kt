package com.project.trackernity.ui.fragments

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.project.trackernity.databinding.CustomInfoWindowBinding

class CustomInfoWindow(context:Context):GoogleMap.InfoWindowAdapter {
    var mContext = context
    private var _binding: CustomInfoWindowBinding? = null
    private val binding get() = _binding!!

    private fun infoWindowText(marker: Marker){
        _binding = CustomInfoWindowBinding.inflate(LayoutInflater.from(mContext))

        binding.snippet.text = marker.snippet

    }

    override fun getInfoContents(marker: Marker): View {
        infoWindowText(marker)
        return binding.root
    }

    override fun getInfoWindow(marker: Marker): View {
        infoWindowText(marker)
        return binding.root
    }
}