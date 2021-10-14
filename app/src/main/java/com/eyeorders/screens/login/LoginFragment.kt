package com.eyeorders.screens.login

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.eyeorders.screens.main.MainActivity
import com.eyeorders.util.extension.hideKeyboard
import com.eyeorders.util.imageloader.ImageLoader
import com.eyeorders.util.message.ToastHelper
import com.eyeorders.util.printer.IposPrinterManger
import com.eyeorders.util.viewbinding.viewBinding
import com.tasleem.orders.R
import com.tasleem.orders.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    @Inject
    lateinit var imageLoader: ImageLoader

    @Inject
    lateinit var toastHelper: ToastHelper

    private val binding by viewBinding(FragmentLoginBinding::bind)
    private val viewModel: LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageLoader.loadDrawableRes(
            R.drawable.ellipse,
            binding.topCover
        )

        imageLoader.loadDrawableRes(
            R.drawable.login_logo,
            binding.appIcon
        )

        binding.passwordEditText.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                v.hideKeyboard()
                validateThenLogin()
                true
            } else {
                false
            }
        }

        binding.loginBtn.setOnClickListener {
            it.hideKeyboard()
            validateThenLogin()
        }




        binding.creditsLabel.text = getString(
            R.string.powered_by_label,
            Calendar.getInstance().get(Calendar.YEAR).toString()
        )

        viewModel.viewState.observe(viewLifecycleOwner) {
            binding.progress.root.isVisible = it.loading
            binding.loginBtn.isEnabled = it.loading.not()
            binding.emailEditText.error = it.emailMessage
            binding.passwordEditText.error = it.passwordMessage
            it.errorMessage?.let { message ->
                toastHelper.showMessage(message)
            }

            if(it.loggedIn){
                startActivity(MainActivity.getStartIntent(requireContext()))
                requireActivity().finish()
            }
        }
    }



    private fun validateThenLogin() {
        if (isValidDetails()) {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            viewModel.login(email, password)
        }
    }

    private fun isValidDetails(): Boolean {
        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()
        if (email.isEmpty() ||
            password.isEmpty() ||
            email.matches(Patterns.EMAIL_ADDRESS.toRegex()).not()
        ) {

            if (email.matches(Patterns.EMAIL_ADDRESS.toRegex()).not()) {
                binding.emailEditText.error = getString(R.string.email_valid_required_msg)
            }

            if (password.isEmpty()) {
                binding.passwordEditText.error = getString(R.string.password_required_msg)
            }

            return false
        }

        return true
    }
}