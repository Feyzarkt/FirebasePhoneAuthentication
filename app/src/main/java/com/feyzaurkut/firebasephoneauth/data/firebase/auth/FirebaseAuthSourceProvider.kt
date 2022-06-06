package com.feyzaurkut.firebasephoneauth.data.firebase.auth

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthSourceProvider @Inject constructor(): FirebaseAuthSource {

    override suspend fun loginWithCredential(authCredential: AuthCredential): FirebaseUser {

        val firebaseAuthInstance = FirebaseAuth.getInstance()
        firebaseAuthInstance.signInWithCredential(authCredential).await()

        return firebaseAuthInstance.currentUser ?: throw FirebaseAuthException("","")
    }
}