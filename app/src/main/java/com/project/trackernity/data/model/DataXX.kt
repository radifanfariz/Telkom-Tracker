package com.project.trackernity.data.model


import com.google.gson.annotations.SerializedName

data class DataXX(
    @SerializedName("date_time")
    val dateTime: String?,
    @SerializedName("user_id")
    val userId: String?
)