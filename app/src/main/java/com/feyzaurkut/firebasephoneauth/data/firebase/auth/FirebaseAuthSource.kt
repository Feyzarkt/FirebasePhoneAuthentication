package com.feyzaurkut.firebasephoneauth.data.firebase.auth

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser

interface FirebaseAuthSource {
    suspend fun loginWithCredential(authCredential: AuthCredential) : FirebaseUser?
}