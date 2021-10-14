package com.eyeorders.util.overlay

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.RequiresApi
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class OverlayPermissionContract @Inject constructor(
    @ActivityContext private val context: Context,
) : ActivityResultContract<Unit, Boolean>() {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun createIntent(context: Context, input: Unit?): Intent {
        return OverlayHelper.overlayIntent(context.packageName)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
        return OverlayHelper.hasOverlayPermission(context)
    }

}