package com.project.trackernity.data.model


import com.google.gson.annotations.SerializedName

data class TrackernityRequestThird(
    @SerializedName("regional")
    val regional: String?,
    @SerializedName("witel")
    val witel: String?,
    @SerializedName("unit")
    val unit: String?,
    @SerializedName("descriptions")
    val descriptions: String?,
    @SerializedName("notes")
    val notes: String?,
    @SerializedName("perkiraan_jarak_gangguan")
    val perkiraanJarakGangguan: String?,
    @SerializedName("remarks")
    val remarks: String?,
    @SerializedName("remarks2")
    val remarks2: String?,
    @SerializedName("user_id")
    val userId: String?,
    @SerializedName("order_query")
    val orderQuery: String?,
    @SerializedName("real_remarks")
    val realRemarks: String?
)