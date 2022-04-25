package com.project.trackernity.ui.fragments.data

import com.project.trackernity.data.TrackernityApi
import com.project.trackernity.data.model.DropdownItemsAuth
import com.project.trackernity.data.model.SignUpRequest
import com.project.trackernity.data.model.SignUpResponse
import com.project.trackernity.ui.fragments.data.model.LoggedInUser
import timber.log.Timber
import java.io.IOException

class SignUpRepository(private val api: TrackernityApi) {
    suspend fun signUp(signUpRequest: SignUpRequest):Result<SignUpResponse>{
        try {
            val response = api.signUp(signUpRequest)
            Timber.d("API Data: ${response.body()}")
            if (response.isSuccessful){
                val signUpResponse = response.body()
                return Result.Success(signUpResponse!!)
            }
            val erroMsg = response.errorBody()?.string()
            Timber.d("Error API: ${erroMsg}")
            return Result.Error(IOException(erroMsg))

//            throw Exception("Terjadi kesalahan saat melakukan request data, status error ${response.code()}")

//            val fakeUser = LoggedInUser(java.util.UUID.randomUUID().toString(), "Jane Doe")
//            return Result.Success(fakeUser)
        } catch (ex: Throwable) {
            Timber.d("Error Response : ${ex}")
            return Result.Error(IOException(ex))
        }

    }

    suspend fun getDropdwonItemsAuth():Result<DropdownItemsAuth>{
        try {
            val response = api.getDropdownItemsAuth()
            Timber.d("API Data: ${response.body()}")
            if (response.isSuccessful){
                val dropdownItemsAuthResponse = response.body()
                return Result.Success(dropdownItemsAuthResponse!!)
            }
            val erroMsg = response.errorBody()?.string()
            Timber.d("Error API: ${erroMsg}")
            return Result.Error(IOException(erroMsg))

//            throw Exception("Terjadi kesalahan saat melakukan request data, status error ${response.code()}")

//            val fakeUser = LoggedInUser(java.util.UUID.randomUUID().toString(), "Jane Doe")
//            return Result.Success(fakeUser)
        } catch (ex: Throwable) {
            Timber.d("Error Response : ${ex}")
            return Result.Error(IOException(ex))
        }

    }
}