package com.project.trackernity.data.model


import com.google.gson.annotations.SerializedName

data class SignUpResponse(
    @SerializedName("code")
    val code: String?,
    @SerializedName("data")
    val `data`: List<DataXX>?,
    @SerializedName("status")
    val status: String?
)