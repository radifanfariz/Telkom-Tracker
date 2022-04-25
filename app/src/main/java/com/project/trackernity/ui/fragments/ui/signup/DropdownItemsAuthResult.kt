package com.project.trackernity.ui.fragments.ui.signup

data class DropdownItemsAuthResult(
    val loading:Boolean = false,
    val error: String? = null,
    val code: List<String?>? = null,
    val regional:List<String?>? = null,
    val witel:List<String?>? = null,
    val unit:List<String?>? = null,

)
