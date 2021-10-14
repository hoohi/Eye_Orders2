package com.eyeorders.screens.orderdetail

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.eyeorders.data.model.OrderStatus
import com.eyeorders.navigator.Navigator
import com.eyeorders.screens.orderdetail.model.OrderTax
import com.eyeorders.screens.orderdetail.tax.OrderTaxAdapter
import com.eyeorders.util.SpaceItemDecoration
import com.eyeorders.util.extension.*
import com.eyeorders.util.livedata.extension.observeEvent
import com.eyeorders.util.message.ToastHelper
import com.eyeorders.util.viewbinding.viewBinding
import com.tasleem.orders.R
import com.tasleem.orders.databinding.FragmentOrderDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class OrderDetailFragment : Fragment(R.layout.fragment_order_detail) {

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var toastHelper: ToastHelper

    private val binding by viewBinding(FragmentOrderDetailBinding::bind)
    private val viewModel: OrderDetailViewModel by viewModels()
    private val orderId by lazy { arguments?.getInt(ORDER_KEY)!! }
    private val orderStatus by lazy { arguments?.getInt(ORDER_STATUS_KEY) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.init(orderStatus)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        orderStatus?.also {
            Timber.d("Order status: $it")
            initOrderStatus(it)
        }

        binding.toolbar.setNavigationOnClickListener {
            navigator.navigateUp()
        }

        binding.declineButton.setOnClickListener {
            viewModel.declineOrder()
        }

        binding.declineButton.beInvisibleIf(orderStatus != OrderStatus.PENDING)

        initMenu()

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.fetchOrderDetail(orderId)
        }

        binding.detailsButton.setOnClickListener {
            viewModel.changeOrderStatus()
        }

        val adapter = OrderDetailAdapter()
        binding.recyclerView.adapter = adapter


        viewModel.showToast.observeEvent(viewLifecycleOwner) {
            toastHelper.showMessage(it)
        }

        viewModel.viewState.observe(viewLifecycleOwner) {
            binding.swipeRefresh.isRefreshing = it.loading
            binding.detailsButton.isEnabled = it.loading.not()
            binding.toolbar.menu.findItem(R.id.item_print).isEnabled = it.success

            val order = it.orderDetail
            binding.titleTextView.text = getString(
                R.string.order_detail_order_title,
                order.orderNum.toString(),
                order.id.toString()
            )

            if (it.success) {
                binding.declineButton.beInvisibleIf(it.orderDetail.status != OrderStatus.PENDING)
            }
            adapter.submitList(order.products)
            binding.orderNumberTextView.text =
                getString(R.string.order_detail_order_label, order.orderNum.toString())

            binding.customerNameTextView.text = order.customerName
            binding.customerZoneTextView.text = order.customerZone
            binding.customerPhoneTextView.text = order.customerPhone

            binding.subtotalTextView.text =
                getString(R.string.amount_currency, order.subTotal.toString())

            initNotes(order.notes)
            initVendorDiscount(order.vendorDiscount)
            initTasleemDiscount(order.tasleemDiscount)
            initDeliveryFee(order.deliveryPrice)
            initOrderStatus(order.status)
            initTaxes(order.taxes)

            it.errorMessage?.let { message ->
                toastHelper.showMessage(message)
            }
        }

        viewModel.fetchOrderDetail(orderId)
    }

    private fun initNotes(orderComment: String) {
        if (orderComment.isNotEmpty() && orderComment != "null") {
            binding.notesTextView.isVisible = true
            binding.notesTextView.text = orderComment
        } else {
            binding.notesTextView.isVisible = false
        }
    }

    private fun initMenu() {
        binding.toolbar.inflateMenu(R.menu.order_detail_menu)
        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.item_print -> {
                    viewModel.printOrderDetailsIposPrinter()
                    viewModel.printOrderDetails()
                    true
                }

                else -> false
            }
        }

    }

    private fun initDeliveryFee(deliveryPrice: Double) {
        binding.deliveryFeeLabelTextView.beVisibleIf(deliveryPrice.isGreaterThanZero())
        binding.deliveryFeeTextView.beVisibleIf(deliveryPrice.isGreaterThanZero())
        binding.deliveryFeeTextView.text =
            getString(R.string.amount_currency, deliveryPrice.formatMoney())
    }

    private fun initTasleemDiscount(tasleemDiscount: Double) {
        binding.tasleemDiscountLabelTextView.beVisibleIf(tasleemDiscount.isGreaterThanZero())
        binding.tasleemDiscountTextView.beVisibleIf(tasleemDiscount.isGreaterThanZero())
        binding.tasleemDiscountTextView.text =
            getString(R.string.amount_currency, tasleemDiscount.formatMoney())
    }

    private fun initVendorDiscount(vendorDiscount: Double) {
        binding.vendorDiscountLabelTextView.beVisibleIf(vendorDiscount.isGreaterThanZero())
        binding.vendorDiscountTextView.beVisibleIf(vendorDiscount.isGreaterThanZero())
        binding.vendorDiscountTextView.text =
            getString(R.string.amount_currency, vendorDiscount.formatMoney())
    }

    private fun initTaxes(taxes: List<OrderTax>) {
        Timber.d("TAX: $taxes")
        val adapter = OrderTaxAdapter()
        binding.taxRecyclerView.adapter = adapter
        binding.taxRecyclerView.addItemDecoration(
            SpaceItemDecoration(
                topSpace = resources.getDimensionPixelSize(R.dimen.menu_space),
                bottomSpace = resources.getDimensionPixelSize(R.dimen.menu_space)
            )
        )
        adapter.submitList(taxes)
        binding.taxRecyclerView.beVisibleIf(taxes.isNotEmpty())
    }

    private fun initOrderStatus(orderStatus: Int) {
        Timber.d("initOrderStatus: $orderStatus")
        when (orderStatus) {
            OrderStatus.PENDING -> {
                binding.detailsTextView.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.pink)
                )
                binding.detailsButton.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), R.color.pink)
                )
                binding.detailsButton.text = getString(R.string.order_detail_btn_option_accept)
                binding.detailsTextView.text =
                    getString(R.string.order_detail_message_option_accept)
                binding.detailsTextView.beVisible()
                binding.detailsButton.beVisible()
            }

            OrderStatus.VENDOR_CONFIRM, OrderStatus.DRIVER_PENDING -> {
                Timber.d("Hello here VENDOR_CONFIRM")
                binding.detailsButton.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), R.color.blue)
                )
                binding.detailsTextView.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.blue)
                )
                binding.detailsButton.text = getString(R.string.order_detail_btn_option_ready)
                binding.detailsTextView.text =
                    getString(R.string.order_detail_message_option_ready)
                binding.detailsTextView.beVisible()
                binding.detailsButton.beVisible()
            }

            OrderStatus.VENDOR_CANCELED -> {
                binding.detailsTextView.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.red)
                )

                binding.detailsTextView.text =
                    getString(R.string.order_detail_message_option_vendor_cancelled)
                binding.detailsTextView.beVisible()
                binding.detailsButton.beGone()
            }

            OrderStatus.CANCELED -> {
                binding.detailsTextView.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.red)
                )

                binding.detailsTextView.text =
                    getString(R.string.order_detail_message_option_cancelled)
                binding.detailsTextView.beVisible()
                binding.detailsButton.beGone()
            }

            else -> {
                binding.detailsTextView.beGone()
                binding.detailsButton.beGone()
            }
        }
    }

    companion object {
        private const val ORDER_KEY = "orderId"
        private const val ORDER_STATUS_KEY = "order_status"
        fun makeArguments(orderId: Int, orderStatus: Int? = null): Bundle {
            Timber.d("Order status makeArguments: $orderStatus")
            val args = Bundle()
            args.putInt(ORDER_KEY, orderId)
            orderStatus?.also {
                args.putInt(ORDER_STATUS_KEY, it)
            }
            return args
        }
    }
}