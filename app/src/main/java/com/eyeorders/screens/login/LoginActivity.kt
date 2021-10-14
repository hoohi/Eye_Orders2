package com.eyeorders.screens.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.eyeorders.data.model.DataState
import com.tasleem.orders.R
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        viewModel.logoutUser()
        viewModel.logoutResult.observe(this) { result ->
            when (result) {
                is DataState.Success -> {
                    Timber.i("Logged user out completely")
                }

                is DataState.Error -> {
                    Timber.i("Error logging user out completely")
                }

                is DataState.Loading -> {
                    //ignore
                }
            }
        }
    }

    companion object {
        fun getStartIntent(context: Context): Intent {
            return Intent(context, LoginActivity::class.java)
        }
    }
}