package com.feyzaurkut.firebasephoneauth.ui

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.feyzaurkut.firebasephoneauth.R
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val auth : FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        navController = navHostFragment.navController

        checkUserLogin()
    }

    private fun checkUserLogin(){
        if(auth.currentUser != null)
            navController.navigate(R.id.action_loginFragment_to_homeFragment)
    }

}