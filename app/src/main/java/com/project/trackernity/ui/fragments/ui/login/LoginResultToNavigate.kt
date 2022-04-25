package com.project.trackernity.ui.fragments.ui.login

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Parcelize
@Keep data class LoginResultToNavigate(
    val nama:String? = null,
    val regional:String? = null,
    val witel: String? = null,
    val unit: String? = null,
    val c_profile: String? = null,
    val userId: String? = null,
    val token: String? = null,
    val flagging: String? = null
):Parcelable
