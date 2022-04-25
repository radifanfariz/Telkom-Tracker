package com.project.trackernity.viewmodels

data class DropdownItemsOthersResult (
    val loading: Boolean = false,
    val error: String? = null,
    val notesItems: List<String?>? = null,
    val descItems: List<String?>? = null,
)