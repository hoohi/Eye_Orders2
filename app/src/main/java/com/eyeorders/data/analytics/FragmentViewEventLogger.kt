package com.eyeorders.data.analytics

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import javax.inject.Inject

class FragmentViewEventLogger @Inject constructor(
    private val fragmentManager: FragmentManager,
    private val analytics: Analytics,
    private val fragmentScreenViewMapper: FragmentScreenViewMapper,
) : DefaultLifecycleObserver {

    private val fragmentLifecycleCallback = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
            analytics.logEvent(fragmentScreenViewMapper.convert(f))
        }
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        fragmentManager.registerFragmentLifecycleCallbacks(fragmentLifecycleCallback, true)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        fragmentManager.registerFragmentLifecycleCallbacks(fragmentLifecycleCallback, true)
    }

    fun observe(lifecycleOwner: LifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(this)
    }
}