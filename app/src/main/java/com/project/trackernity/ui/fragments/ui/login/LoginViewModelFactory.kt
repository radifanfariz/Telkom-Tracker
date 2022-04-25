package com.project.trackernity.ui.fragments.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.project.trackernity.data.TrackernityApi
import com.project.trackernity.other.Constants
import com.project.trackernity.ui.fragments.data.LoginDataSource
import com.project.trackernity.ui.fragments.data.LoginRepository
import com.project.trackernity.util.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
class LoginViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(
                loginRepository = LoginRepository(
                    dataSource = LoginDataSource(api = Retrofit.Builder()
                        .baseUrl(Constants.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                        .create(TrackernityApi::class.java))
                ),
                dispatchers = object : DispatcherProvider {
                    override val main: CoroutineDispatcher
                        get() = Dispatchers.Main
                    override val io: CoroutineDispatcher
                        get() = Dispatchers.IO
                    override val default: CoroutineDispatcher
                        get() = Dispatchers.Default
                    override val unconfined: CoroutineDispatcher
                        get() = Dispatchers.Unconfined
                }
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}