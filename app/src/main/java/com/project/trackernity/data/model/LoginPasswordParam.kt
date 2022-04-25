package com.project.trackernity.data.model

import com.google.gson.annotations.SerializedName

data class LoginPasswordParam(
    @SerializedName("iv")
    val iv: String,
    @SerializedName("encryptedData")
    val encryptedData: String
)
