package com.eyeorders.screens.testconnection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.eyeorders.screens.base.BaseBottomSheetDialog
import com.eyeorders.util.extension.beGone
import com.eyeorders.util.extension.beVisible
import com.eyeorders.util.network.ConnectionChecker
import com.eyeorders.util.network.ConnectionState
import com.eyeorders.util.network.NetworkListener
import com.eyeorders.util.network.NetworkType
import com.eyeorders.util.viewbinding.viewBinding
import com.tasleem.orders.R
import com.tasleem.orders.databinding.DialogTestConnectionBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject

@AndroidEntryPoint
class TestConnectionDialog : BaseBottomSheetDialog() {

    @Inject
    lateinit var connectionChecker: ConnectionChecker

    @Inject
    lateinit var networkListener: NetworkListener


    private val binding by viewBinding(DialogTestConnectionBinding::bind)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_test_connection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cancelButton.setOnClickListener {
            dismiss()
        }

        binding.retryButton.setOnClickListener {
            startConnectionCheck()
        }


        viewLifecycleOwner.lifecycle.addObserver(networkListener)
        networkListener.networkStateLive.observe(this) {
            when (networkListener.getConnectionType()) {
                NetworkType.TYPE_VPN -> {
                    binding.connectionTypeTextView.text = getString(R.string.connection_type, "VPN")
                }
                NetworkType.TYPE_MOBILE -> {
                    binding.connectionTypeTextView.text =
                        getString(R.string.connection_type, "3G/4G")
                }
                NetworkType.TYPE_WIFI -> {
                    binding.connectionTypeTextView.text =
                        getString(R.string.connection_type, "WIFI")
                }
            }
        }

        startConnectionCheck()
    }

    private fun startConnectionCheck() {
        showLoading()
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            delay(1000)
            connectionChecker.evaluateConnection { state ->
                updateUIConnectionState(state)
            }
        }
    }

    private fun updateUIConnectionState(state: ConnectionState) {
        hideLoading()
        when (state) {
            ConnectionState.CONNECTED -> {
                binding.statusImage.setImageResource(R.drawable.ic_check_green)
                binding.messageTextView.text =
                    getString(R.string.test_connection_success_msg)
            }

            ConnectionState.DISCONNECTED -> {
                binding.statusImage.setImageResource(R.drawable.ic_cancel)
                binding.messageTextView.text = getString(R.string.test_connection_fail_msg)
            }

            ConnectionState.SLOW -> {
                binding.statusImage.setImageResource(R.drawable.ic_check_green)
                binding.messageTextView.text = getString(R.string.test_connection_slow_msg)
            }
        }
    }

    private fun showLoading() {
        binding.progressBar.beVisible()
        binding.statusImage.beGone()
        binding.retryButton.isEnabled = false
    }

    private fun hideLoading() {
        binding.progressBar.beGone()
        binding.statusImage.beVisible()
        binding.retryButton.isEnabled = true
    }

    companion object {
        fun newInstance(): TestConnectionDialog {
            val args = Bundle()
            val fragment = TestConnectionDialog()
            fragment.arguments = args
            return fragment
        }
    }
}