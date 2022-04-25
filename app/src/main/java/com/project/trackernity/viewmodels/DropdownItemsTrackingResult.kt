package com.project.trackernity.viewmodels

data class DropdownItemsTrackingResult (
    val loading: Boolean = false,
    val error: String? = null,
    val label: String? = null,
    val treg: List<String?>? = null,
    val witel: List<String?>? = null,
    val head: List<String?>? = null,
    val tail: List<String?>? = null,
    val all: List<String?>? = null,
    val route: List<String?>? = null,
        )