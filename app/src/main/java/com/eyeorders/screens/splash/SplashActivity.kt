package com.eyeorders.screens.splash

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.eyeorders.data.prefs.PrefsDataManager
import com.eyeorders.data.pushnotification.pushy.PushyClient
import com.eyeorders.screens.login.LoginActivity
import com.eyeorders.screens.main.MainActivity
import com.eyeorders.screens.neworder.NewOrderOverlay
import com.eyeorders.util.mediaplayer.SoundPlayer
import com.eyeorders.util.notification.AppNotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var overlay: NewOrderOverlay

    @Inject
    lateinit var prefsDataManager: PrefsDataManager

    @Inject
    lateinit var soundPlayer: SoundPlayer

    @Inject
    lateinit var appNotificationHelper: AppNotificationHelper

    @Inject
    lateinit var pushyInitializer: PushyClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pushyInitializer.listen(this)
        soundPlayer.stop()
        overlay.hideOverlay()
        intent.extras?.printAllItems()
        appNotificationHelper.cancelOrderNotification()
        lifecycleScope.launch {
            if (prefsDataManager.loggedIn()) {
                startActivity(MainActivity.getStartIntent(this@SplashActivity))
            } else {
                startActivity(LoginActivity.getStartIntent(this@SplashActivity))
            }
            finish()
        }
    }

    private fun Bundle.printAllItems(){
        for (key in keySet()) {
            Timber.d( "Item: key: $key - value: ${get(key)}")
        }
    }

    companion object {
        const val DATA_KEY = "data"
        fun getStartIntent(context: Context, data: String): Intent {
            val intent = Intent(context, SplashActivity::class.java)
            intent.putExtra(DATA_KEY, data)
            return intent
        }
    }
}