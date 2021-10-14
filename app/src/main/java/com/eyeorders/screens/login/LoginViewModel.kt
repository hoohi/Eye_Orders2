package com.eyeorders.screens.login

import android.os.Parcelable
import androidx.lifecycle.*
import com.eyeorders.data.model.DataState
import com.eyeorders.data.usecase.auth.LoginUseCase
import com.eyeorders.data.usecase.auth.LogoutUseCase
import com.eyeorders.util.livedata.extension.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.scan
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val mutableViewState = MutableLiveData<LoginViewState>()
    val viewState = mutableViewState.asLiveData()


    private val stateReducer = { oldState: LoginViewState, dataState: DataState<Unit> ->
        when (dataState) {
            is DataState.Error -> oldState.copy(
                loading = false,
                errorMessage = dataState.message,
                emailMessage = null,
                passwordMessage = null,
            )
            is DataState.Loading -> oldState.copy(loading = true, errorMessage = null)
            is DataState.Success -> oldState.copy(
                loading = false,
                loggedIn = true,
                errorMessage = null,
                emailMessage = null,
                passwordMessage = null,
            )
        }
    }

    private val logoutIntent = MutableLiveData<Unit>()

    val logoutResult = logoutIntent.switchMap {
        logoutUseCase.execute().asLiveData()
    }

    init {
        savedStateHandle.getLiveData<Params>(PARAM_KEY)
            .asFlow()
            .flatMapLatest {
                loginUseCase.execute(it.email, it.password)
            }
            .scan(LoginViewState()) { previous, result ->
                stateReducer(previous, result)
            }.onEach {
                mutableViewState.value = it
            }
            .launchIn(viewModelScope)
    }

    fun login(email: String, password: String) {
        savedStateHandle.set(PARAM_KEY, Params(email, password))
    }

    fun logoutUser() {
        logoutIntent.postValue(Unit)
    }

    @Parcelize
    data class Params(
        val email: String,
        val password: String,
    ) : Parcelable

    companion object {
        private const val PARAM_KEY = "page_key"
    }
}