package com.eyeorders.screens.productavailability

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.eyeorders.screens.base.BaseBottomSheetDialog
import com.eyeorders.util.viewbinding.viewBinding
import com.tasleem.orders.R
import com.tasleem.orders.databinding.DialogProductAvailabilityBinding

typealias ProductAvailabilityCallback = (ProductAvailability) -> Unit

class ProductAvailabilityDialog : BaseBottomSheetDialog() {

    private val binding by viewBinding(DialogProductAvailabilityBinding::bind)
    private val availability by lazy {
        arguments?.getParcelable<ProductAvailability>(
            AVAILABILITY_KEY
        )!!
    }
    private val name by lazy { arguments?.getString(NAME_KEY)!! }

    private var productAvailability: ProductAvailabilityCallback? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_product_availability, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.availableRoot.setOnClickListener {

            binding.availableCheck.isChecked = true
            binding.unavailableCheck.isChecked = false

            if(availability != ProductAvailability.AVAILABLE){
                productAvailability?.invoke(ProductAvailability.AVAILABLE)
            }
            dismiss()
        }

        binding.unavailableRoot.setOnClickListener {
            binding.availableCheck.isChecked = false
            binding.unavailableCheck.isChecked = true
            if(availability != ProductAvailability.UNAVAILABLE){
                productAvailability?.invoke(ProductAvailability.UNAVAILABLE)
            }
            dismiss()
        }

        when (availability) {
            ProductAvailability.AVAILABLE -> {
                val title = getString(R.string.product_available_title, name)
                val message = getString(R.string.product_available_msg)

                binding.titleTextView.text = title
                binding.messageTextView.text = message

                binding.availableCheck.isChecked = true
                binding.unavailableCheck.isChecked = false
            }

            ProductAvailability.UNAVAILABLE -> {
                val title = getString(R.string.product_unavailable_title, name)
                val message = getString(R.string.product_unavailable_msg)

                binding.titleTextView.text = title
                binding.messageTextView.text = message

                binding.availableCheck.isChecked = false
                binding.unavailableCheck.isChecked = true
            }
        }

    }

    companion object {
        private const val AVAILABILITY_KEY = "availability"
        private const val NAME_KEY = "name"

        fun newInstance(
            productName: String,
            availability: ProductAvailability,
            callback: ProductAvailabilityCallback
        ): ProductAvailabilityDialog {
            val args = Bundle()
            args.putParcelable(AVAILABILITY_KEY, availability)
            args.putString(NAME_KEY, productName)
            val fragment = ProductAvailabilityDialog()
            fragment.arguments = args
            fragment.productAvailability = callback
            return fragment
        }
    }
}