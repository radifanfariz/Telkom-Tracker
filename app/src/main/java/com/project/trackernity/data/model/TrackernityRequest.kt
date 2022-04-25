package com.project.trackernity.data.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

data class TrackernityRequest(
    @SerializedName("nama") val nama: String?,
    @SerializedName("regional") val regional: String?,
    @SerializedName("witel") val witel: String?,
    @SerializedName("unit") val unit: String?,
    @SerializedName("c_profile") val c_profile: String?,
    @SerializedName("user_id") val user_id: String?,
    @SerializedName("lat") val lat: Double?,
    @SerializedName("lgt") val lgt: Double?,
    @SerializedName("continuity") val continuity: Int?,
    @SerializedName("remarks") val remarks: String?,
    @SerializedName("token") val token:String?,
)