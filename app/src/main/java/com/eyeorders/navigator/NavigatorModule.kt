package com.eyeorders.navigator

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
interface NavigatorModule {

    companion object {
        @Provides
        @ActivityScoped
        fun provideNavControllerProvider(activity: Activity): NavComponentsProvider {
            return (activity as NavComponentsProvider)
        }

        @Provides
        @ActivityScoped
        fun provideFragmentManager(activity: Activity): FragmentManager {
            return (activity as AppCompatActivity).supportFragmentManager
        }
    }
}
