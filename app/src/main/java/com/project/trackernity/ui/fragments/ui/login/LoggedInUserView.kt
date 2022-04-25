package com.project.trackernity.ui.fragments.ui.login

import android.os.Parcelable
import com.project.trackernity.data.model.LoginResponse
import kotlinx.android.parcel.Parcelize

/**
 * User details post authentication that is exposed to the UI
 */
data class LoggedInUserView(
    val userData: LoginResponse,
    //... other data fields that may be accessible to the UI
)

