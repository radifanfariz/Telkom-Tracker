package com.project.trackernity.data.model

class TrackernityResponseSecond : ArrayList<TrackernityResponseSecondItem>(){
    data class TrackernityAllDataResponse(
        var trackernityData: MutableList<TrackernityResponseSecond>
    )
}
