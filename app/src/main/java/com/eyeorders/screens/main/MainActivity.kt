package com.eyeorders.screens.main

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.eyeorders.data.analytics.FragmentViewEventLogger
import com.eyeorders.data.eventbus.EventBus
import com.eyeorders.data.eventbus.event.LogoutEvent
import com.eyeorders.data.model.DataState
import com.eyeorders.data.prefs.PrefsDataManager
import com.eyeorders.data.pushnotification.NotificationParser
import com.eyeorders.navigator.NavComponentsProvider
import com.eyeorders.navigator.Navigator
import com.eyeorders.neworderschecker.AppService
import com.eyeorders.screens.login.LoginActivity
import com.eyeorders.screens.neworder.CheckForNewOrdersWorker
import com.eyeorders.screens.neworder.NewOrderOverlay
import com.eyeorders.util.imageloader.ImageLoader
import com.eyeorders.util.language.LanguageHelper
import com.eyeorders.util.mediaplayer.NoInternetSoundPlayer
import com.eyeorders.util.mediaplayer.SoundPlayer
import com.eyeorders.util.message.ToastHelper
import com.eyeorders.util.network.NetworkListener
import com.eyeorders.util.notification.AppNotificationHelper
import com.eyeorders.util.overlay.OverlayHelper
import com.eyeorders.util.overlay.OverlayPermissionContract
import com.eyeorders.util.viewbinding.viewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tasleem.orders.BuildConfig
import com.tasleem.orders.R
import com.tasleem.orders.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavComponentsProvider {

    @Inject
    lateinit var imageLoader: ImageLoader

    @Inject
    lateinit var eventBus: EventBus

    @Inject
    lateinit var prefsDataManager: PrefsDataManager

    @Inject
    lateinit var appNotificationHelper: AppNotificationHelper

    @Inject
    lateinit var networkListener: NetworkListener

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var toastHelper: ToastHelper

    @Inject
    lateinit var notificationParser: NotificationParser

    @Inject
    lateinit var noInternetSoundPlayer: NoInternetSoundPlayer

    @Inject
    lateinit var soundPlayer: SoundPlayer

    @Inject
    lateinit var overlayPermissionContract: OverlayPermissionContract

    @Inject
    lateinit var overlay: NewOrderOverlay

    @Inject
    lateinit var fragmentViewEventLogger: FragmentViewEventLogger

    @Inject
    lateinit var languageHelper: LanguageHelper

    private lateinit var requestPermission: ActivityResultLauncher<Unit>

    private val binding by viewBinding(ActivityMainBinding::inflate)
    private val viewModel: MainViewModel by viewModels()


    override val navController: NavController
        get() = (supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController

    override val drawerLayout: DrawerLayout
        get() = binding.drawerLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("SOund player: $soundPlayer")
        overlay.hideOverlay()
        soundPlayer.stop()
        fragmentViewEventLogger.observe(this)
        appNotificationHelper.cancelOrderNotification()
        setContentView(binding.root)

        AppService.start(this)

        requestPermission = registerForActivityResult(overlayPermissionContract) {
            if (it.not()) {
                showOverlayPermissionDialog()
            } else {
                toastHelper.showMessage(getString(R.string.overlay_permission_granted_msg))
            }
        }

        networkListener.bindToLifecycle(lifecycle)
        networkListener.networkStateLive.observe(this) { connected ->
            if (connected) {
                noInternetSoundPlayer.stop()
            } else {
                toastHelper.showMessage(getString(R.string.internet_disconnected_msg))
                noInternetSoundPlayer.play()
                CheckForNewOrdersWorker.scheduleWork(this)
            }
        }

        setupNavItems()

        setUpLanguage()

        eventBus.events
            .filterIsInstance<LogoutEvent>()
            .onEach {
                logOut()
            }.launchIn(lifecycleScope)


        updateLayoutDirection()
        updateVendorDetails()
        observeLogout()
        handleNewOrder()
        showOverlayPermissionDialog()
    }

    private fun showOverlayPermissionDialog() {
        if (OverlayHelper.hasOverlayPermission(this).not()) {
            val dialog = MaterialAlertDialogBuilder(this)
                .setTitle(R.string.overlay_permission_title)
                .setMessage(R.string.overlay_permission_msg)
                .setPositiveButton(
                    android.R.string.ok
                ) { _, _ ->
                    requestOverlayPermissionIfNotGranted()
                }.setNegativeButton(
                    android.R.string.cancel
                ) { dialog, _ ->
                    dialog.dismiss()
                }
            dialog.setCancelable(true)
            dialog.show()
        }
    }

    private fun requestOverlayPermissionIfNotGranted() {
        if (OverlayHelper.hasOverlayPermission(this).not()) {

            requestPermission.launch(Unit)
        }
    }

    private fun handleNewOrder() {
//        if (intent.extras?.containsKey(DATA_KEY) == true) {
//            val data = intent.extras?.getString(DATA_KEY)
//            notificationParser.getNotificationExtras(data)?.also { extra->
//                Timber.d("OrderID: ${extra.orderId}")
//                navigator.toOrderDetailDeeLink(extra.orderId)
//            }
//        }
    }

    private fun observeLogout() {
        viewModel.logoutResult.observe(this) { result ->
            when (result) {
                is DataState.Success -> {
                    startActivity(LoginActivity.getStartIntent(this@MainActivity))
                    finish()
                }

                is DataState.Error -> {
                    toastHelper.showMessage(result.message)
                }

                is DataState.Loading -> {
                    //ignore
                }
            }
        }
    }

    private fun logOut() {
        lifecycleScope.launch {
            prefsDataManager.setLoggedIn(false)
            startActivity(LoginActivity.getStartIntent(this@MainActivity))
            finish()
        }
    }

    private fun setUpLanguage() {
        val locale = languageHelper.getCurrentLanguage()

        binding.navItems.languageSpinner.setSelection(
            when (locale) {
                LanguageHelper.LANGUAGE_ARABIC -> 1
                LanguageHelper.LANGUAGE_ENGLISH -> 0
                else -> 0
            }, false
        )

        binding.navItems.languageSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapter: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    p3: Long
                ) {

                    val langIso = when (adapter?.adapter?.getItem(position)) {
                        getString(R.string.lang_english) -> Pair(
                            LanguageHelper.LANGUAGE_ENGLISH,
                            LanguageHelper.COUNTRY_ENGLISH
                        )
                        getString(R.string.lang_arabic) -> Pair(
                            LanguageHelper.LANGUAGE_ARABIC,
                            LanguageHelper.COUNTRY_ARABIC
                        )
                        else -> Pair(
                            LanguageHelper.LANGUAGE_ENGLISH,
                            LanguageHelper.COUNTRY_ENGLISH
                        )
                    }

                    val lang = languageHelper.getCurrentLanguage()
                    Timber.d("lang: $lang")
                    val newLang = langIso.first
                    if (newLang != lang) {

                        Timber.d("Changing locale: $langIso")
                        languageHelper.changeLanguage(
                            newLang,
                            langIso.second
                        )
                        lifecycleScope.launch {
                            prefsDataManager.setLanguage(newLang)
                        }
                        recreate()
                    }


                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }
            }
    }

    private fun setupNavItems() {
        binding.navItems.version.text = getString(
            R.string.version_prefix,
            BuildConfig.VERSION_NAME
        )
        binding.navView.setupWithNavController(navController)

        binding.navItems.activeOrders.setOnClickListener {
            navigator.toActiveOrders()
            closeDrawer()
        }

        binding.navItems.menuManagement.setOnClickListener {
            navigator.toMenuManagement()
            closeDrawer()
        }

        binding.navItems.recentOrders.setOnClickListener {
            navigator.toRecentOrders()
            closeDrawer()
        }

        binding.navItems.workHours.setOnClickListener {
            navigator.toWorkHours()
            closeDrawer()
        }

        binding.navItems.testConnection.setOnClickListener {
            navigator.toTestConnection()
            closeDrawer()
        }

        binding.navItems.help.setOnClickListener {
            navigator.toHelp()
            closeDrawer()
        }

        binding.navItems.logout.setOnClickListener {
            logOut()
            closeDrawer()
        }
    }

    private fun updateVendorDetails() {
        viewModel.vendorDetails.observe(this) {
            binding.navItems.titleTextView.text = it.name
            imageLoader.load(it.imageUrl, binding.navItems.icon, R.drawable.eye_orders)
        }
    }

    @Suppress("DEPRECATION")
    private fun Configuration.getLocaleCompat(): Locale {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) locales.get(0) else locale
    }

    private fun updateLayoutDirection() {
        val lang = languageHelper.getCurrentLanguage()

        if (lang == LanguageHelper.LANGUAGE_ARABIC) {
            window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
        } else {
            window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LOCALE
        }
    }

    private fun closeDrawer() {
        binding.drawerLayout.closeDrawer(GravityCompat.START)
    }

    companion object {
        private const val TAG = "MainActivity"
        fun getStartIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}