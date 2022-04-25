package com.project.trackernity.ui.fragments.ui.login

import android.content.Context
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.trackernity.R
import com.project.trackernity.data.model.LoginRequest
import com.project.trackernity.repositories.TrackingRepository
import com.project.trackernity.ui.fragments.data.LoginRepository
import com.project.trackernity.ui.fragments.data.Result
import com.project.trackernity.util.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    private val _userData = TrackingRepository.userData
    val userData : LiveData<LoginResultToNavigate>
        get() = _userData

    private val _userDataFromSharedPreference = TrackingRepository.userDataFromSharedPreference
    val userDataSharedPreferences : LiveData<LoginResultToNavigate>
        get() = _userDataFromSharedPreference

    fun login(username: String, password: String) {
        // can be launched in a separate asynchronous job
        viewModelScope.launch(dispatchers.io) {
            val pass = loginRepository.encryptPassword(password)
            val result = loginRepository.login(LoginRequest(userId = username, pass = pass))
            Timber.d("Result API: ${result}")

            if (result is Result.Success) {
                val userData = result.data.userData!!
                val userDataItem = userData.data!!.last().user!!
                _userData.postValue(
                        LoginResultToNavigate(
                            nama = userDataItem.nama,
                        regional = userDataItem.regional,
                        witel = userDataItem.witel,
                            unit = userDataItem.unit,
                            c_profile = userDataItem.cProfile,
                            userId = userDataItem.userid,
                            token = userData.data.last().token,
                            flagging = userDataItem.flagging
                    )
                )
                _loginResult.postValue(
                    LoginResult(success = LoggedInUserView(userData = userData))
                )
            }else if(result is Result.Error){
                val erroMsg = result.exception.message
                _loginResult.postValue(LoginResult(error = erroMsg))
            } else {
                _loginResult.postValue(LoginResult(error = "Something is Wrong!"))
            }
        }
        Timber.d("login result API: ${_loginResult.value}")
    }

    fun logout(){
        _userData.value = null
        _userDataFromSharedPreference.value = null
        onCleared()
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains("@")) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}