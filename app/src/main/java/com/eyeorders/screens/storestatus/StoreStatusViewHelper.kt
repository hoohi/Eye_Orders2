package com.eyeorders.screens.storestatus

import android.content.Context
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import com.eyeorders.data.model.DataState
import com.eyeorders.util.imageloader.PlaceHolderDrawable
import com.google.android.material.button.MaterialButton
import com.tasleem.orders.R
import dagger.hilt.android.qualifiers.ActivityContext
import timber.log.Timber
import javax.inject.Inject

class StoreStatusViewHelper @Inject constructor(
    @ActivityContext private val context: Context,
    private val fragmentManager: FragmentManager,
) {

    private lateinit var button: MaterialButton
    private lateinit var coroutineScope: LifecycleCoroutineScope
    private lateinit var viewLifeCycleOwner: LifecycleOwner
    private lateinit var storeState: LiveData<DataState<StoreStatus>>
    private lateinit var onStoreStatusChange: (StoreStatus) -> Unit

    private var storeStatus = StoreStatus.BUSY

    fun initialize(
        button: MaterialButton,
        viewLifeCycleOwner: LifecycleOwner,
        storeState: LiveData<DataState<StoreStatus>>,
        onStoreStatusChange: (StoreStatus) -> Unit,
    ) {
        this.button = button
        this.storeState = storeState
        this.viewLifeCycleOwner = viewLifeCycleOwner
        this.coroutineScope = viewLifeCycleOwner.lifecycleScope
        this.onStoreStatusChange = onStoreStatusChange
        init()
    }

    private fun init() {
        button.setOnClickListener {
            coroutineScope.launchWhenResumed {
                val dialog = StoreStatusDialog.newInstance(storeStatus) {
                    onStoreStatusChange.invoke(it)
                    updateStoreStatus(it)
                }
                dialog.show(fragmentManager, dialog.javaClass.name)
            }
        }

        storeState.observe(viewLifeCycleOwner) { state ->
            Timber.d("STORE STATE: $state")
            when (state) {
                is DataState.Loading -> {
                    button.icon = PlaceHolderDrawable(
                        context,
                        context.resources.getDimension(R.dimen.button_loading_radius),
                        context.resources.getDimension(R.dimen.button_loading_stroke),
                        R.color.white
                    )
                }

                is DataState.Success -> {
                    storeStatus = state.data
                    updateStoreStatus(storeStatus)
                }

                is DataState.Error -> {
                    updateStoreStatus(storeStatus)
                    Toast.makeText(
                        context,
                        context.getString(R.string.update_store_state_failed_msg),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun updateStoreStatus(storeStatus: StoreStatus) {
        when (storeStatus) {
            StoreStatus.OPEN -> {
                button.icon = ContextCompat.getDrawable(context, R.drawable.point)
                button.text = context.getString(R.string.store_open_status_text)
            }

            StoreStatus.CLOSED -> {
                button.icon = ContextCompat.getDrawable(context, R.drawable.point_red)
                button.text = context.getString(R.string.store_closed_status_text)
            }

            StoreStatus.BUSY -> {
                button.icon = null
                button.text = ""
            }
        }
    }

}