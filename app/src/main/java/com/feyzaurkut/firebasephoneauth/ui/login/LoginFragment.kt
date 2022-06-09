package com.feyzaurkut.firebasephoneauth.ui.login

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.feyzaurkut.firebasephoneauth.R
import com.feyzaurkut.firebasephoneauth.databinding.FragmentLoginBinding
import com.feyzaurkut.firebasephoneauth.util.RequestState
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

const val PHONE_NUM_LENGTH = 10

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var phoneNum: String
    private lateinit var mVerificationId: String
    private lateinit var mResendingToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var countDownTimer: CountDownTimer
    private val mAuth: FirebaseAuth = Firebase.auth
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)

        checkAuthStatus()
        initListeners()

        return binding.root
    }


    private fun initListeners() {

        with(binding) {

            btnSendCode.setOnClickListener {
                phoneNum = tvCountryCode.text.toString().trim() + etMobileNumberInput.text.toString().trim()
                if (phoneNum.length < PHONE_NUM_LENGTH) {
                    Toast.makeText(requireContext(), getString(R.string.phone_num_is_short), Toast.LENGTH_SHORT).show()
                } else {
                    startPhoneNumberVerification(phoneNum)
                    binding.progressBar.isVisible = true
                }
            }

            btnContinue.setOnClickListener {
                if (otpView.text.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), getString(R.string.otp_code_is_empty), Toast.LENGTH_SHORT).show()
                } else {
                    loginWithPhone(otpView.text.toString())
                }
            }

            tvResendCode.setOnClickListener {
                resendPhoneNumberVerification(phoneNum, mResendingToken)
                pauseTimer()
            }

            otpView.addTextChangedListener {
                tvWrongCode.text = ""
                otpView.setTextColor(ContextCompat.getColor(requireContext(), R.color.purple))
                otpView.setItemBackground(getDrawable(requireContext(), R.drawable.bg_purple_border))
            }

            etMobileNumberInput.addTextChangedListener {
                tvErrorMessage.text = ""
                tvCountryCode.setBackgroundResource(R.drawable.bg_purple_border)
                etMobileNumberInput.setBackgroundResource(R.drawable.bg_purple_border)
                tvCountryCode.setTextColor(ContextCompat.getColor(requireContext(), R.color.purple))
                etMobileNumberInput.setTextColor(ContextCompat.getColor(requireContext(), R.color.purple))
            }
        }

    }

    private fun startPhoneNumberVerification(phoneNum: String) {
        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(phoneNum)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(getCallBack())
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun resendPhoneNumberVerification(
        phoneNum: String,
        token: PhoneAuthProvider.ForceResendingToken
    ) {
        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(phoneNum)
            .setForceResendingToken(token)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(getCallBack())
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun loginWithPhone(smsCode: String) {
        val credential = PhoneAuthProvider.getCredential(mVerificationId, smsCode)
        viewModel.loginWithCredential(credential)
    }

    private fun checkAuthStatus() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.authState.collect { requestState ->
                    when (requestState) {
                        is RequestState.Loading -> {
                            binding.progressBar.isVisible = true
                        }
                        is RequestState.Failure -> {
                            binding.progressBar.isVisible = false
                            if (requestState.exception is FirebaseAuthInvalidCredentialsException) {
                                with(binding) {
                                    tvWrongCode.text = getString(R.string.code_is_wrong)
                                    otpView.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                                    otpView.setItemBackground(getDrawable(requireContext(), R.drawable.bg_red_border))
                                }
                            }
                        }
                        is RequestState.Success -> {
                            binding.progressBar.isVisible = false
                            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                            Toast.makeText(requireContext(), getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                        }
                    }

                }
            }
        }
    }

    private fun getCallBack(): PhoneAuthProvider.OnVerificationStateChangedCallbacks {
        return object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onCodeSent(
                verificationId: String,
                forceResendingToken: PhoneAuthProvider.ForceResendingToken
            ) {
                binding.progressBar.isVisible = false
                mVerificationId = verificationId
                mResendingToken = forceResendingToken

                with(binding) {
                    llPhoneAuth.isVisible = false
                    binding.otpView.requestFocus()
                    llVerifyCode.isVisible = true
                    if (!btnContinue.isEnabled) {
                        binding.btnContinue.isEnabled = true
                    }
                }
                startTimer()
            }

            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                //if you want to go directly to the home page without otpView, you can use this part
                //val code = phoneAuthCredential.smsCode
                //if (code != null) loginWithPhone(code)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                binding.progressBar.isVisible = false
                Log.e("fail", "$e")
                with(binding) {
                    tvErrorMessage.text = getString(R.string.phone_num_is_wrong)
                    tvCountryCode.setBackgroundResource(R.drawable.bg_red_border)
                    etMobileNumberInput.setBackgroundResource(R.drawable.bg_red_border)
                    tvCountryCode.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                    etMobileNumberInput.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                }
                if (e is FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(requireContext(), getString(R.string.invalid_request), Toast.LENGTH_SHORT).show()
                } else if (e is FirebaseTooManyRequestsException) {
                    Toast.makeText(requireContext(), getString(R.string.sms_quota_is_full), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(120000, 1000) {
            override fun onFinish() {
                binding.btnContinue.isEnabled = false
            }

            override fun onTick(p0: Long) {
                val minute = (p0 / 1000) / 60
                val seconds = (p0 / 1000) % 60
                binding.tvTimer.text = String.format("%02d:%02d", minute, seconds)
            }
        }.start()
    }

    private fun pauseTimer() {
        countDownTimer.cancel()
    }
}