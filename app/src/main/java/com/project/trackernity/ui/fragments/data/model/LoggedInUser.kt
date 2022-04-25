package com.project.trackernity.ui.fragments.data.model

import com.project.trackernity.data.model.LoginResponse

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class LoggedInUser(
    val userData: LoginResponse?,
)