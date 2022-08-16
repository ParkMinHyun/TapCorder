package com.android.tabcorder

import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.widget.Toast
import com.android.tabcorder.util.ActivityLifeCycleCallback
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    init {
        instance = this
    }

    private val activityLifeCycleCallbacks = ActivityLifeCycleCallback()

    override fun onCreate() {
        super.onCreate()

        registerActivityLifecycleCallbacks(activityLifeCycleCallbacks)
    }

    override fun onTerminate() {
        super.onTerminate()

        unregisterActivityLifecycleCallbacks(activityLifeCycleCallbacks)
    }

    companion object {
        lateinit var instance: App

        fun getContext(): Context = instance.applicationContext

        fun getResources(): Resources = instance.applicationContext.resources

        fun getCurrentActivity() = instance.activityLifeCycleCallbacks.currentActivity

        fun showToast(msg: String) = Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show()
    }
}