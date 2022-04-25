package com.project.trackernity.data.model


import com.google.gson.annotations.SerializedName

data class DropdownItemsAuthItem(
    @SerializedName("code")
    val code: String?,
    @SerializedName("regional")
    val regional: String?,
    @SerializedName("unit")
    val unit: String?,
    @SerializedName("witel")
    val witel: String?
)