package com.project.trackernity.data.model


import com.google.gson.annotations.SerializedName

data class TrackernityRequestSecond(
    @SerializedName("regional")
    val regional: String?,
    @SerializedName("witel")
    val witel: String?,
    @SerializedName("unit")
    val unit: String?,
    @SerializedName("user_id")
    val userId: String?,
    @SerializedName("lat")
    val lat: Double?,
    @SerializedName("lgt")
    val lgt: Double?,
    @SerializedName("continuity")
    val continuity: String?,
    @SerializedName("remarks")
    val remarks: String?,
    @SerializedName("remarks2")
    val remarks2: String?,
    @SerializedName("descriptions")
    val descriptions: String?,
    @SerializedName("notes")
    val notes: String?,
    @SerializedName("id_perkiraan")
    val idPerkiraan: Int?,

)