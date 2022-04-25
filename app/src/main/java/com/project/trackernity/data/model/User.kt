package com.project.trackernity.data.model


import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("c_profile")
    val cProfile: String?,
    @SerializedName("unit")
    val unit: String?,
    @SerializedName("flagging")
    val flagging: String?,
    @SerializedName("handphone")
    val handphone: String?,
    @SerializedName("idx")
    val idx: Int?,
    @SerializedName("idy")
    val idy: Int?,
    @SerializedName("imei")
    val imei: String?,
    @SerializedName("nama")
    val nama: String?,
    @SerializedName("pass")
    val pass: String?,
    @SerializedName("regional")
    val regional: String?,
    @SerializedName("team")
    val team: String?,
    @SerializedName("userid")
    val userid: String?,
    @SerializedName("witel")
    val witel: String?
)