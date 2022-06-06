package com.feyzaurkut.firebasephoneauth.domain.usecase

import com.feyzaurkut.firebasephoneauth.data.repository.FirebaseRepository
import com.feyzaurkut.firebasephoneauth.util.RequestState
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FirebaseAuthUseCase @Inject constructor(private val firebaseRepository: FirebaseRepository) {

    suspend operator fun invoke(authCredential: AuthCredential) = flow {
        emit(RequestState.Loading())
        try {
            emit(RequestState.Success(firebaseRepository.loginWithCredential(authCredential)))
        } catch (e: FirebaseAuthException) {
            emit(RequestState.Failure(e))
        }
    }
}