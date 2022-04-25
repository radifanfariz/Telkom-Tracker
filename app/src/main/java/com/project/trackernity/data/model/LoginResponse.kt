package com.project.trackernity.data.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class LoginResponse(
    @SerializedName("code")
    val code: String?,
    @SerializedName("data")
    val `data`: List<DataX>?,
    @SerializedName("status")
    val status: String?
)