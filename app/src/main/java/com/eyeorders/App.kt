package com.eyeorders

import androidx.hilt.work.HiltWorkerFactory
import androidx.multidex.MultiDexApplication
import androidx.work.Configuration
import com.eyeorders.data.pushnotification.pushy.PushyClient
import com.eyeorders.util.language.LanguageHelper
import com.eyeorders.util.logger.TimberInitializer
import com.eyeorders.util.printer.sumni.SunmiPrintHelper
import com.google.firebase.database.FirebaseDatabase
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : MultiDexApplication(),  Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var firebaseDatabase: FirebaseDatabase

    @Inject
    lateinit var pusherInitializer: PushyClient

    @Inject
    lateinit var timberInitializer: TimberInitializer

    @Inject
    lateinit var languageHelper: LanguageHelper

    override fun onCreate() {
        super.onCreate()
        initTimber()
        initThreeTen()
        initFirebaseDatabase()
        initPrinter()
        initPusher()
        initLanguageHelper()
    }

    private fun initLanguageHelper() {
        languageHelper.init()
    }

    private fun initPusher() {
        pusherInitializer.initPusher()
    }

    private fun initPrinter() {
        SunmiPrintHelper.getInstance().initSunmiPrinterService(this)
    }

    private fun initFirebaseDatabase() {
        firebaseDatabase.setPersistenceEnabled(true)
    }


    private fun initThreeTen() {
        AndroidThreeTen.init(this)
    }

    private fun initTimber() {
        timberInitializer.init()
    }

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}