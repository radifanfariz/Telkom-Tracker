package com.project.trackernity.data.model


import com.google.gson.annotations.SerializedName

data class DropdownItemsOthersItems(
    @SerializedName("descriptions")
    val descriptions: List<String>,
    @SerializedName("notes")
    val notes: List<String>
)