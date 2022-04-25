package com.project.trackernity.ui.fragments.ui.signup

data class SignUpFormState(
    val usernameError: Int? = null,
    val passwordError: Int? = null,
    val passwordRepeatError: Int? = null,
    val nameError: Int? = null,
    val regionalError: Int? = null,
    val witelError: Int? = null,
    val unitError: Int? = null,
    val isDataValid: Boolean = false,
)
