package com.project.trackernity.data.model


import com.google.gson.annotations.SerializedName

data class DropdownItem(
    @SerializedName("treg")
    val treg: List<String>?,
    @SerializedName("witel")
    val witel: List<String>?,
    @SerializedName("remarks")
    val remarks: DropdownItemRemarksItems?,
)

data class DropdownItemRemarksItems(
    @SerializedName("Head")
    val head: List<String>?,
    @SerializedName("Tail")
    val tail: List<String>?,
    @SerializedName("All")
    val all: List<String>?
)