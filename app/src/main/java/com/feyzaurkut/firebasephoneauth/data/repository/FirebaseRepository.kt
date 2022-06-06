package com.feyzaurkut.firebasephoneauth.data.repository

import com.feyzaurkut.firebasephoneauth.data.firebase.auth.FirebaseAuthSourceProvider
import com.google.firebase.auth.AuthCredential
import javax.inject.Inject


class FirebaseRepository @Inject constructor(
    private val firebaseAuthSourceProvider: FirebaseAuthSourceProvider
) {
    suspend fun loginWithCredential(authCredential: AuthCredential) =
        firebaseAuthSourceProvider.loginWithCredential(authCredential)
}