package com.project.trackernity.ui.fragments.ui.signup

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.trackernity.R
import com.project.trackernity.data.model.SignUpRequest
import com.project.trackernity.ui.fragments.data.Result
import com.project.trackernity.ui.fragments.data.SignUpRepository
import com.project.trackernity.util.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    val signUpRepository: SignUpRepository,
    val dispatchers:DispatcherProvider
    ):ViewModel() {
    private val _signUpForm = MutableLiveData<SignUpFormState>()
    val signUpFormState: LiveData<SignUpFormState> = _signUpForm

    private val _signUpResult = MutableLiveData<SignUpResult>()
    val signUpResult: LiveData<SignUpResult> = _signUpResult

    private val _dropdownItemsAuthResult = MutableLiveData<DropdownItemsAuthResult>()
    val dropdownItemsAuthResult: LiveData<DropdownItemsAuthResult> = _dropdownItemsAuthResult

    fun getDropdownItemsAuth(){
        _dropdownItemsAuthResult.value = DropdownItemsAuthResult(loading = true)
        viewModelScope.launch(dispatchers.io) {
            val result = signUpRepository.getDropdwonItemsAuth()
            Timber.d("Result API: ${result}")

            if (result is Result.Success) {
                val codeList = result.data.mapNotNull { it.code }.toList()
                val regionalList = result.data.mapNotNull { it.regional }.toList()
                val witelList = result.data.mapNotNull { it.witel }.toList()
                val unitList = result.data.mapNotNull { it.unit }.toList()
                _dropdownItemsAuthResult.postValue(
                    DropdownItemsAuthResult(
                        loading = false,
                        error = null,
                        code = codeList,
                        regional = regionalList,
                        witel = witelList,
                        unit = unitList
                    )
                )
            }else if(result is Result.Error){
                val erroMsg = result.exception.message
                _dropdownItemsAuthResult.postValue(DropdownItemsAuthResult(loading = false,error = erroMsg))
            } else {
                _dropdownItemsAuthResult.postValue(DropdownItemsAuthResult(loading = false,error = "Something is Wrong!"))
            }
        }
        Timber.d("dropdown item auth result API: ${_dropdownItemsAuthResult.value}")
    }

    fun signUp(param:SignUpRequest){
            viewModelScope.launch(dispatchers.io) {
                val result = signUpRepository.signUp(param)
                Timber.d("Result API: ${result}")

                if (result is Result.Success) {
                    val successMsg = result.data.status
                    _signUpResult.postValue(
                        SignUpResult(success = successMsg)
                    )
                }else if(result is Result.Error){
                    val erroMsg = result.exception.message
                    _signUpResult.postValue(SignUpResult(error = erroMsg))
                } else {
                    _signUpResult.postValue(SignUpResult(error = "Something is Wrong!"))
                }
            }
            Timber.d("signUp result API: ${_signUpResult.value}")
    }

    fun signUpDataChanged(username: String, password: String, passwordRepeat: String, name: String, regional: String, witel: String,unit:String) {
        if (!isUserNameValid(username)) {
            _signUpForm.value = SignUpFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password) ) {
            _signUpForm.value = SignUpFormState(passwordError = R.string.invalid_password)
        } else if (!isPasswordValid(passwordRepeat) ) {
            _signUpForm.value = SignUpFormState(passwordRepeatError = R.string.invalid_password)
        } else if(!isOtherValid(name)){
            _signUpForm.value = SignUpFormState(nameError = R.string.invalid_input)
        } else if(!isOtherValid(regional)){
            _signUpForm.value = SignUpFormState(regionalError = R.string.invalid_dropdown_item)
        }else if(!isOtherValid(witel)){
            _signUpForm.value = SignUpFormState(witelError = R.string.invalid_dropdown_item)
        }else if(!isOtherValid(unit)){
            _signUpForm.value = SignUpFormState(unitError = R.string.invalid_dropdown_item)
        }
        else {
            _signUpForm.value = SignUpFormState(isDataValid = true)
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

    private fun isOtherValid(other: String):Boolean{
        return (other != "")
    }
}