package com.rzahr.quicktools

import android.app.Activity
import android.app.Application
import android.os.Bundle

class ActivityLifeCycleCallbacks : Application.ActivityLifecycleCallbacks {

    var currentActivity: Activity? = null

    override fun onActivityPaused(activity: Activity?) {
        currentActivity = activity
    }

    override fun onActivityResumed(activity: Activity?) {
        currentActivity = activity
    }

    override fun onActivityStarted(activity: Activity?) {
        currentActivity = activity
    }

    override fun onActivityDestroyed(activity: Activity?) {
        //currentActivity = activity
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
        // currentActivity = activity
    }

    override fun onActivityStopped(activity: Activity?) {
        // currentActivity = activity
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        currentActivity = activity
    }
}