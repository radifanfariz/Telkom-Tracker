package com.project.trackernity.data.model

data class TrackernityResponse(
    val code: String,
    val `data`: List<Data>,
    val status: String
)