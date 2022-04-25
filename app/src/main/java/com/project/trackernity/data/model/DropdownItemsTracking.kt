package com.project.trackernity.data.model


import com.google.gson.annotations.SerializedName

data class DropdownItemsTracking(
    @SerializedName("dropdown_item")
    val dropdownItem: DropdownItem?
)