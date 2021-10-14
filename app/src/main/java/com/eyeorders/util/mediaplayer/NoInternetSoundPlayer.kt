package com.eyeorders.util.mediaplayer

import android.content.Context
import android.media.MediaPlayer
import com.eyeorders.data.analytics.AnalyticEvent
import com.eyeorders.data.analytics.Analytics
import com.tasleem.orders.R
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoInternetSoundPlayer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val analytics: Analytics,
) {

    private lateinit var mediaPlayer: MediaPlayer
    private var stopped = true

    fun play() {
        Timber.d("Play sound")
        if (stopped) {
            mediaPlayer = MediaPlayer.create(context, R.raw.no_internet_alarm)
            mediaPlayer.start()
            stopped = false
            Timber.d("Stopped: $stopped")
            analytics.logEvent(AnalyticEvent.PlayLostConnectionSound)
        }
    }

    fun stop() {
        Timber.d("Stop sound")
        if (this::mediaPlayer.isInitialized) {
            if (stopped.not()) {
                mediaPlayer.stop()
                mediaPlayer.reset()
                mediaPlayer.release()
                stopped = true
                Timber.d("Stopped: $stopped")
                analytics.logEvent(AnalyticEvent.StopLostConnectionSound)
            }
        }
    }

}