package com.project.trackernity.repositories

import androidx.lifecycle.MutableLiveData
import com.project.trackernity.data.TrackernityApi
import com.project.trackernity.data.model.*
import com.project.trackernity.ui.fragments.data.Result
import timber.log.Timber
import java.io.IOException

class DropdownItemsRepository(private val api: TrackernityApi) {
    suspend fun getDropdownItemsTracking(): Result<DropdownItemsTracking> {
        try {
            val response = api.getDropdownItemsTracking()
            Timber.d("API Data: ${response.body()}")
            if (response.isSuccessful){
                val dropdownItemsAuthTracking = response.body()
                return Result.Success(dropdownItemsAuthTracking!!)
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

    suspend fun getDropdownItemsTregs(): Result<DropdownItemsTrackingSecond> {
        try {
            val response = api.getDropdownItemsTregs()
            Timber.d("API Data: ${response.body()}")
            if (response.isSuccessful){
                val dropdownItemsAuthTracking = response.body()
                return Result.Success(dropdownItemsAuthTracking!!)
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

    suspend fun getDropdownItemsWitels(treg: String): Result<DropdownItemsTrackingSecond> {
        try {
            val response = api.getDropdownItemsWitels(treg)
            Timber.d("API Data: ${response.body()}")
            if (response.isSuccessful){
                val dropdownItemsAuthTracking = response.body()
                return Result.Success(dropdownItemsAuthTracking!!)
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

    suspend fun getDropdownItemsRemarks(treg: String,witel:String): Result<DropdownItemsTrackingSecondRemarks> {
        try {
            val response = api.getDropdownItemsRemarks(treg,witel)
            Timber.d("API Data: ${response.body()}")
            if (response.isSuccessful){
                val dropdownItemsAuthTracking = response.body()
                return Result.Success(dropdownItemsAuthTracking!!)
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

    suspend fun getDropdownItemsRoutes(treg: String,witel: String,remarks:String): Result<DropdownItemsTrackingSecond> {
        try {
            val response = api.getDropdownItemsRoutes(treg,witel,remarks)
            Timber.d("API Data: ${response.body()}")
            if (response.isSuccessful){
                val dropdownItemsAuthTracking = response.body()
                return Result.Success(dropdownItemsAuthTracking!!)
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

    suspend fun getDropdownItemsOthers(): Result<DropdownItemsOthers> {
        try {
            val response = api.getDropdownItemsOthers()
            Timber.d("API Data: ${response.body()}")
            if (response.isSuccessful){
                val dropdownItemsOthers = response.body()
                return Result.Success(dropdownItemsOthers!!)
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
    companion object{
        val orderQuery = MutableLiveData("")
    }
}