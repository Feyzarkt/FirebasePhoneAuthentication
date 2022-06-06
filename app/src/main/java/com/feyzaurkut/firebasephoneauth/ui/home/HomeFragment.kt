package com.feyzaurkut.firebasephoneauth.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
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
        onBackPressed()

        return binding.root
    }

    private fun initListeners() {
        with(binding) {
            btnFloatingAct.setOnClickListener {
                auth.signOut()
                findNavController().popBackStack()
                Toast.makeText(requireContext(), getString(R.string.logout_success), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onBackPressed() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }
}