package com.feyzaurkut.firebasephoneauth.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.feyzaurkut.firebasephoneauth.domain.usecase.FirebaseAuthUseCase
import com.feyzaurkut.firebasephoneauth.util.RequestState
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val firebaseAuthUseCase: FirebaseAuthUseCase,
) : ViewModel() {

    private var _authState = MutableStateFlow<RequestState<FirebaseUser>?>(null)
    val authState: StateFlow<RequestState<FirebaseUser>?> = _authState

    fun loginWithCredential(authCredential: AuthCredential) {
        viewModelScope.launch {
            firebaseAuthUseCase.invoke(authCredential).collect { authResult ->
                _authState.value = authResult
            }
        }
    }
}