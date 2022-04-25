package com.project.trackernity.data.model


import com.google.gson.annotations.SerializedName

data class DataX(
    @SerializedName("date_time")
    val dateTime: String?,
    @SerializedName("token")
    val token: String?,
    @SerializedName("user")
    val user: User?
)