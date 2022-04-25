package com.project.trackernity.data.model


import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("user_id")
    val userId: String?,
    @SerializedName("pass")
    val pass: LoginPasswordParam?
)