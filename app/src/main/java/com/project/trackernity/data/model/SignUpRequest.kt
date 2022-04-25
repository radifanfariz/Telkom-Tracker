package com.project.trackernity.data.model


import com.google.gson.annotations.SerializedName

data class SignUpRequest(
    @SerializedName("nama")
    val nama: String?,
    @SerializedName("pass")
    val pass: String?,
    @SerializedName("pass_repeat")
    val passRepeat: String?,
    @SerializedName("regional")
    val regional: String?,
    @SerializedName("user_id")
    val userId: String?,
    @SerializedName("witel")
    val witel: String?,
    @SerializedName("unit")
    val unit: String?,
    @SerializedName("c_profile")
    val c_profile: String?
)