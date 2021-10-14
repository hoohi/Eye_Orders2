package com.eyeorders.neworderschecker

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.eyeorders.data.dispatcher.AppDispatchers
import com.eyeorders.data.prefs.PrefsDataManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() , CoroutineScope {

    @Inject
    lateinit var dispatchers: AppDispatchers

    @Inject
    lateinit var prefsDataManager: PrefsDataManager

    private val job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = dispatchers.main + job


    override fun onReceive(context: Context, intent: Intent?) {
        if(intent?.action == Intent.ACTION_BOOT_COMPLETED){
            launch {
                if(prefsDataManager.loggedIn()){
                    AppService.start(context)
                }
            }
        }
    }

    companion object {
        fun setEnabled(context: Context, enable: Boolean) {
            val flag =
                if (enable) PackageManager.COMPONENT_ENABLED_STATE_ENABLED else PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            val component =
                ComponentName(context, BootReceiver::class.java)
            context.packageManager
                .setComponentEnabledSetting(
                    component, flag,
                    PackageManager.DONT_KILL_APP
                )
        }
    }
}