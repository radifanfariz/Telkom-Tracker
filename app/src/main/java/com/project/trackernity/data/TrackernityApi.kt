package com.project.trackernity.data

import com.project.trackernity.data.model.*
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface TrackernityApi {

    ////////Alpro
    @GET("/api/trackernity/alpro")
    suspend fun getData(@Query("remarks") param:String):Response<MutableList<TrackernityResponseSecondItem>>

    ////////Alpro
    @GET("/api/trackernity/alpro-third")
    suspend fun getData(@Query("treg") param:String,@Query("witel") param2:String,@Query("remarks") param3:String,@Query("descriptions") param4:String,):Response<MutableList<TrackernityResponseSecondItem>>

    @GET("/api/trackernity/gangguan")
    suspend fun getDataSecond(@Query("remarks") param:String):Response<MutableList<TrackernityResponseSecondItem>>

    @GET("/api/trackernity/perkiraan")
    suspend fun getDataThird(@Query("remarks") param:String):Response<MutableList<TrackernityResponseSecondItem>>

//    @Headers("Content-Type: application/json")
//    @POST("/api/trackernity/alpro")
//    suspend fun sendDataSecond(
//        @Body trackernityRequestSecond: TrackernityRequestSecond): Response<TrackernityResponse>

    @Headers("Content-Type: application/json")
    @POST("/api/trackernity/perkiraan")
    suspend fun sendDataThird(
        @Body trackernityRequestThird: TrackernityRequestThird): Response<TrackernityResponse>

    @Headers("Content-Type: application/json")
    @POST("/api/trackernity/gangguan")
    suspend fun sendDataSecond(
        @Body trackernityRequestSecond: TrackernityRequestSecond): Response<TrackernityResponse>


    ///////Teknisi
    @Headers("Content-Type: application/json")
    @POST("/api/trackernity")
    suspend fun sendData(
        @Body trackernityRequest: TrackernityRequest): Response<TrackernityResponse>

    ///////Login
    @Headers("Content-Type: application/json")
    @POST("/api/trackernity/login")
    suspend fun login(
        @Body loginRequest: LoginRequest): Response<LoginResponse>

    ///////SignUp
    @Headers("Content-Type: application/json")
    @POST("/api/trackernity/sign-up")
    suspend fun signUp(
        @Body signUpRequest: SignUpRequest): Response<SignUpResponse>

    ///////DropdownItemsAuth
    @GET("/api/trackernity/dropdown-item-auth")
    suspend fun getDropdownItemsAuth(): Response<DropdownItemsAuth>

    ///////DropdownItemsTracking
    @GET("/api/trackernity/dropdown-item-second")
    suspend fun getDropdownItemsTracking(): Response<DropdownItemsTracking>

    ///////DropdownItemsTrackingSecond
    @GET("/api/trackernity/dropdown-item-treg")
    suspend fun getDropdownItemsTregs(): Response<DropdownItemsTrackingSecond>

    @GET("/api/trackernity/dropdown-item-witel")
    suspend fun getDropdownItemsWitels(@Query("treg") treg:String): Response<DropdownItemsTrackingSecond>

    @GET("/api/trackernity/dropdown-item-remarks")
    suspend fun getDropdownItemsRemarks(@Query("treg") treg:String,@Query("witel") witel:String): Response<DropdownItemsTrackingSecondRemarks>

    @GET("/api/trackernity/dropdown-item-routes")
    suspend fun getDropdownItemsRoutes(@Query("treg") treg:String,@Query("witel") witel:String,@Query("remarks") remarks:String): Response<DropdownItemsTrackingSecond>

    ///////////////////////////////////////

    ///////DropdownItemsOthers
    @GET("/api/trackernity/dropdown-item-others")
    suspend fun getDropdownItemsOthers(): Response<DropdownItemsOthers>
}