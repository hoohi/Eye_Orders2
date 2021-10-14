@file:Suppress("DEPRECATION")

package com.eyeorders.util.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.*
import android.os.Build
import androidx.annotation.IntDef
import androidx.annotation.IntRange
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import androidx.lifecycle.*
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.eyeorders.util.network.NetworkType.Companion.TYPE_MOBILE
import com.eyeorders.util.network.NetworkType.Companion.TYPE_VPN
import com.eyeorders.util.network.NetworkType.Companion.TYPE_WIFI
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject


class NetworkListener @Inject constructor(@ApplicationContext private val context: Context) :
    LifecycleObserver {
    private val _networkSubject = MutableLiveData<Boolean>()
    private val _networkInfoLive = MutableLiveData<NetworkStateWrapper>()

    val networkStateLive: LiveData<Boolean> = _networkSubject
    val networkInfoLive: LiveData<NetworkStateWrapper> = _networkInfoLive

    private lateinit var receiver: BroadcastReceiver
    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    private val connectivityManager =
        context.applicationContext.getSystemService<ConnectivityManager>()

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun start() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Timber.d("Starting for API >= 21")
            setupConnectionListener()
        } else {
            Timber.d("Starting for API < 21")
            initializeReceiver()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun stop() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Timber.d("Stopping for API >= 21")
            val connectivityManager =
                context.applicationContext.getSystemService<ConnectivityManager>()
            networkCallback?.let {
                connectivityManager?.unregisterNetworkCallback(it)
                networkCallback = null

            }
        } else {
            Timber.d("Stopping for API < 21")
            LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver)
        }
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setupConnectionListener() {
        Timber.d("Setting up connectivity listener")
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onLost(network: Network) {

                Timber.d(" lost connection")
                val networkInfo = connectivityManager?.getNetworkInfo(network)
                val networkStateWrapper = NetworkStateWrapper(false, networkInfo)
                _networkInfoLive.postValue(networkStateWrapper)
                _networkSubject.postValue(false)
            }

            override fun onAvailable(network: Network) {
                Timber.d(" gained connection")
                val networkInfo = connectivityManager?.getNetworkInfo(network)
                val networkStateWrapper = NetworkStateWrapper(true, networkInfo)
                _networkInfoLive.postValue(networkStateWrapper)
                _networkSubject.postValue(true)
            }
        }
        val networkRequest = NetworkRequest.Builder().build()
        networkCallback?.let {
            connectivityManager?.registerNetworkCallback(networkRequest, it)
        }
    }


    @Suppress("DEPRECATION")
    private fun initializeReceiver() {
        Timber.d("Initializing connectivity receiver")
        receiver = ConnectivityReceiver()
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, filter)
    }


    @Suppress("DEPRECATION")
    inner class ConnectivityReceiver() : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Timber.d("OnReceive")
            if (context != null) {
                val stateChange = isOffline()
                Timber.d("Network state changed to: %s", stateChange)
                val networkStateWrapper =
                    NetworkStateWrapper(stateChange, connectivityManager?.activeNetworkInfo)
                _networkInfoLive.postValue(networkStateWrapper)
                _networkSubject.postValue(stateChange)
            }
        }
    }

    @Suppress("DEPRECATION")
    fun isOffline(): Boolean {
        return !(connectivityManager?.activeNetworkInfo != null
                && connectivityManager.activeNetworkInfo?.isConnectedOrConnecting == true)
    }

    @IntRange(from = 0, to = 3)
    fun getConnectionType(): Int {
        var result = 0 // Returns connection type. 0: none; 1: mobile data; 2: wifi
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cm?.run {
                cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                    if (hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        result = TYPE_WIFI
                    } else if (hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        result = TYPE_MOBILE
                    } else if (hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                        result = TYPE_VPN
                    }
                }
            }
        } else {
            cm?.run {
                cm.activeNetworkInfo?.run {
                    if (type == ConnectivityManager.TYPE_WIFI) {
                        result = TYPE_WIFI
                    } else if (type == ConnectivityManager.TYPE_MOBILE) {
                        result = TYPE_MOBILE
                    } else if (type == ConnectivityManager.TYPE_VPN) {
                        result = TYPE_VPN
                    }
                }
            }
        }
        return result
    }

    fun bindToLifecycle(lifecycle: Lifecycle){
        lifecycle.addObserver(this)
    }

}

@IntDef(TYPE_MOBILE, TYPE_VPN, TYPE_WIFI)
annotation class NetworkType {
    companion object {
        const val TYPE_WIFI = 2
        const val TYPE_MOBILE = 1
        const val TYPE_VPN = 3
    }
}

data class NetworkStateWrapper(val connected: Boolean, val networkInfo: NetworkInfo?)
