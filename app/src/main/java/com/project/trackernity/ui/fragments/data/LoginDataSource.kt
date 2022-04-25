package com.project.trackernity.ui.fragments.data

import com.project.trackernity.data.TrackernityApi
import com.project.trackernity.data.model.LoginRequest
import com.project.trackernity.data.model.LoginResponse
import com.project.trackernity.ui.fragments.data.model.LoggedInUser
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource(private val api: TrackernityApi) {

    suspend fun login(loginRequest: LoginRequest): Result<LoggedInUser> {
        try {
            val response = api.login(loginRequest)
            Timber.d("API Data: ${response.body()}")
            if (response.isSuccessful){
                val userData = LoggedInUser(response.body())
                return Result.Success(userData)
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

    fun logout() {

    }
}