package com.rzahr.quicktools

import android.app.Activity
import android.app.Application
import javax.inject.Inject


class Injectable @Inject constructor(var shPrefUtils: ShPrefUtils) {

    val mActivityLifecycleCallbacks = ActivityLifeCycleCallbacks()

    init { instance = this }

    fun registerActivityLifeCycleCallbacks(application: Application) {

        application.registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks)
    }

    companion object {

        private var instance: Injectable? = null

        fun shPrefUtils() : ShPrefUtils {

            return instance!!.shPrefUtils
        }

        fun currentActivity(): Activity? {

            return instance!!.mActivityLifecycleCallbacks.currentActivity
        }
    }
}