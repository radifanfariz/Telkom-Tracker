package com.project.trackernity.di

import android.content.Context
import androidx.room.Room
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.project.trackernity.data.TrackernityApi
import com.project.trackernity.datastore.*
import com.project.trackernity.db.TrackingDAO
import com.project.trackernity.db.TrackingDatabase
import com.project.trackernity.other.Constants
import com.project.trackernity.other.Constants.BASE_URL
import com.project.trackernity.other.Constants.TRACKING_DATABASE_NAME
import com.project.trackernity.repositories.*
import com.project.trackernity.ui.fragments.data.LoginDataSource
import com.project.trackernity.ui.fragments.data.LoginRepository
import com.project.trackernity.ui.fragments.data.SignUpRepository
import com.project.trackernity.util.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTrackingDatabase(
        @ApplicationContext app:Context
    ) = Room.databaseBuilder(
        app,
        TrackingDatabase::class.java,
        TRACKING_DATABASE_NAME
    ).build()

    @Provides
    @Singleton
    fun provideTrackingDao(db:TrackingDatabase) = db.getTrackingDao()

    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(@ApplicationContext appContext: Context) =
        FusedLocationProviderClient(appContext)

    @Singleton
    @Provides
    fun provideTrackernityApi(): TrackernityApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(TrackernityApi::class.java)

    @Singleton
    @Provides
    fun provideTrackernityDropdownItemsRepository(api:TrackernityApi) =
        DropdownItemsRepository(api)

    @Singleton
    @Provides
    fun provideTrackernityListRepository(dao:TrackingDAO,api:TrackernityApi) =
        ListRepository(TrackingRoomDataStore(dao),TrackingRemoteDataStore(api))

    @Singleton
    @Provides
    fun provideTrackernityListSecondRepository(dao:TrackingDAO,api:TrackernityApi) =
        ListSecondRepository(TrackingRoomDataStore(dao),TrackingRemoteDataStoreSecond(api))

    @Singleton
    @Provides
    fun provideTrackernityListThirdRepository(dao:TrackingDAO,api:TrackernityApi) =
        ListThirdRepository(TrackingRoomDataStore(dao),TrackingRemoteDataStoreThird(api))

    @Singleton
    @Provides
    fun provideLoginDataSource(api:TrackernityApi) =
        LoginRepository(LoginDataSource(api))
    @Singleton
    @Provides
    fun provideSignUpRepository(api: TrackernityApi) =
        SignUpRepository(api)

        @Singleton
        @Provides
        fun provideLocationRequest(): LocationRequest = LocationRequest.create().apply {
            interval = Constants.LOCATION_UPDATE_INTERVAL
            fastestInterval = Constants.FASTEST_LOCATION_INTERVAL
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        @Singleton
        @Provides
        fun provideMainRepository(api: TrackernityApi): MainRepository = DefaultMainRepository(api)

        @Singleton
        @Provides
        fun provideDispatchers(): DispatcherProvider = object : DispatcherProvider {
            override val main: CoroutineDispatcher
                get() = Dispatchers.Main
            override val io: CoroutineDispatcher
                get() = Dispatchers.IO
            override val default: CoroutineDispatcher
                get() = Dispatchers.Default
            override val unconfined: CoroutineDispatcher
                get() = Dispatchers.Unconfined
        }
    }
