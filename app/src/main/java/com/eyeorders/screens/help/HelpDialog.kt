package com.eyeorders.screens.help

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.eyeorders.screens.base.BaseBottomSheetDialog
import com.eyeorders.util.intent.DialPhoneUtil
import com.eyeorders.util.intent.SendEmailUtil
import com.eyeorders.util.viewbinding.viewBinding
import com.tasleem.orders.R
import com.tasleem.orders.databinding.DialogHelpBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HelpDialog : BaseBottomSheetDialog() {

    @Inject
    lateinit var sendEmailUtil: SendEmailUtil

    @Inject
    lateinit var dialPhoneUtil: DialPhoneUtil

    private val binding by viewBinding(DialogHelpBinding::bind)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_help, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.phoneTextView.text =
            getString(R.string.support_phone_text, getString(R.string.support_phone))

        binding.emailTextView.setOnClickListener {
            sendEmailUtil.sendEmail(
                getString(R.string.support_email),
                getString(R.string.title_help_and_support)
            )
        }

        binding.phoneTextView.setOnClickListener {
            dialPhoneUtil.dialPhone(getString(R.string.support_phone))
        }
    }
}