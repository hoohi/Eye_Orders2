package com.eyeorders.util.mediaplayer

import android.content.Context
import android.media.MediaPlayer
import com.eyeorders.data.analytics.AnalyticEvent
import com.eyeorders.data.analytics.Analytics
import com.tasleem.orders.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoundPlayer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val analytics: Analytics,
) {

    private lateinit var mediaPlayer: MediaPlayer
    private var stopped = true

    fun play() {
        if (stopped) {
            mediaPlayer = MediaPlayer.create(context, R.raw.tasleem_orders_notify)
            mediaPlayer.start()
            stopped = false
            analytics.logEvent(AnalyticEvent.PlayOrderReceiveSound)
        }
    }

    fun stop() {
        if (this::mediaPlayer.isInitialized) {
            if(stopped.not()){
                mediaPlayer.stop()
                mediaPlayer.reset()
                mediaPlayer.release()
                stopped = true
                analytics.logEvent(AnalyticEvent.StopOrderReceiveSound)
            }
        }
    }
}