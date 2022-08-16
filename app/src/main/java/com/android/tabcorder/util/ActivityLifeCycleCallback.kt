package com.android.tabcorder.util

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log

private const val TAG = "ActivityLifeCycleCallback"

class ActivityLifeCycleCallback : Application.ActivityLifecycleCallbacks {
    
    lateinit var currentActivity: Activity
        private set
    
    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
        Log.d(TAG, "onActivityCreated")
        currentActivity = activity
    }

    override fun onActivityStarted(activity: Activity) {
        Log.d(TAG, "onActivityStarted")
        currentActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {
        Log.d(TAG, "onActivityResumed")
        currentActivity = activity
    }

    override fun onActivityPaused(activity: Activity) {
        Log.d(TAG, "onActivityPaused")
        currentActivity = activity
    }

    override fun onActivityStopped(activity: Activity) {
        Log.d(TAG, "onActivityStopped")
    }

    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {
        Log.d(TAG, "onActivitySaveInstanceState")
    }

    override fun onActivityDestroyed(activity: Activity) {
        Log.d(TAG, "onActivityDestroyed")
    }
}