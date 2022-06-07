package com.feyzaurkut.firebasephoneauth.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.feyzaurkut.firebasephoneauth.R
import com.feyzaurkut.firebasephoneauth.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val auth : FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)

        initListeners()

        return binding.root
    }

    private fun initListeners() {
        with(binding) {
            btnFloatingAct.setOnClickListener {
                auth.signOut()
                findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
                Toast.makeText(requireContext(), getString(R.string.logout_success), Toast.LENGTH_SHORT).show()
            }
        }
    }
}