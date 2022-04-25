package com.project.trackernity.data.model


import com.google.gson.annotations.SerializedName

data class DropdownItemsTrackingSecond(
    @SerializedName("dropdown_items")
    val dropdownItems: List<String>
)

data class DropdownItemsTrackingSecondRemarks(
    @SerializedName("dropdown_items")
    val dropdownItems: DropdownItemRemarksItems
)