package com.eyeorders.screens.neworder

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.eyeorders.navigator.Navigator
import com.eyeorders.util.viewbinding.viewBinding
import com.tasleem.orders.R
import com.tasleem.orders.databinding.FragmentNewOrderBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NewOrderFragment : Fragment(R.layout.fragment_new_order) {

    @Inject
    lateinit var navigator: Navigator

    private val orderId by lazy { arguments?.getInt(ORDER_KEY)!! }

    private val binding by viewBinding(FragmentNewOrderBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.setOnClickListener {
            navigator.fromNewOrderToOrderDetail(orderId)
        }
    }

    companion object {
        private const val ORDER_KEY = "order"
        fun makeArguments(orderId: Int): Bundle {
            val args = Bundle()
            args.putInt(ORDER_KEY, orderId)
            return args
        }
    }

}