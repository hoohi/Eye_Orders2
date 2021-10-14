package com.eyeorders.screens.storestatus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.eyeorders.screens.base.BaseBottomSheetDialog
import com.eyeorders.util.viewbinding.viewBinding
import com.tasleem.orders.R
import com.tasleem.orders.databinding.DialogStoreStatusBinding

typealias ChangeStoreStatusCallback = (StoreStatus) -> Unit

class StoreStatusDialog : BaseBottomSheetDialog() {

    private val binding by viewBinding(DialogStoreStatusBinding::bind)
    private val storeStatus by lazy { arguments?.getParcelable<StoreStatus>(STORE_STATUS_KEY)!! }

    private var changeStoreStatus: ChangeStoreStatusCallback? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_store_status, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.openRoot.setOnClickListener {
            binding.openCheck.isChecked = true
            binding.closeCheck.isChecked = false
            binding.busyCheck.isChecked = false
            changeStoreStatus?.invoke(StoreStatus.OPEN)
            dismiss()
        }

        binding.closeRoot.setOnClickListener {
            binding.openCheck.isChecked = false
            binding.closeCheck.isChecked = true
            binding.busyCheck.isChecked = false
            changeStoreStatus?.invoke(StoreStatus.CLOSED)
            dismiss()
        }

        binding.busyRoot.setOnClickListener {
            binding.openCheck.isChecked = false
            binding.closeCheck.isChecked = false
            binding.busyCheck.isChecked = true
            changeStoreStatus?.invoke(StoreStatus.BUSY)
            dismiss()
        }

        when (storeStatus) {
            StoreStatus.OPEN -> {
                val title = getString(R.string.restaurant_open_title)
                val message = getString(R.string.restaurant_open_msg)
                binding.titleTextView.text = title
                binding.messageTextView.text = message

                binding.openCheck.isChecked = true
                binding.closeCheck.isChecked = false
                binding.busyCheck.isChecked = false
            }

            StoreStatus.CLOSED -> {
                val title = getString(R.string.restaurant_closed_title)
                val message = getString(R.string.restaurant_closed_msg)
                binding.titleTextView.text = title
                binding.messageTextView.text = message

                binding.openCheck.isChecked = false
                binding.closeCheck.isChecked = true
                binding.busyCheck.isChecked = false
            }

            StoreStatus.BUSY -> {
                val title = getString(R.string.restaurant_busy_title)
                val message = getString(R.string.restaurant_busy_msg)
                binding.titleTextView.text = title
                binding.messageTextView.text = message

                binding.openCheck.isChecked = false
                binding.closeCheck.isChecked = false
                binding.busyCheck.isChecked = true
            }
        }
    }

    companion object {
        private const val STORE_STATUS_KEY = "open_key"

        fun newInstance(
            storeStatus: StoreStatus,
            callback: ChangeStoreStatusCallback
        ): StoreStatusDialog {
            val args = Bundle()
            args.putParcelable(STORE_STATUS_KEY, storeStatus)
            val fragment = StoreStatusDialog()
            fragment.arguments = args
            fragment.changeStoreStatus = callback
            return fragment
        }
    }
}