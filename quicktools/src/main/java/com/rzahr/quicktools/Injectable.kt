package com.rzahr.quicktools

import android.app.Activity
import android.app.Application
import android.content.Context
import javax.inject.Inject


class Injectable @Inject constructor(var shPrefUtils: ShPrefUtils) {

    val mActivityLifecycleCallbacks = ActivityLifeCycleCallbacks()
    lateinit var mApplication: Application

    init { instance = this }

    fun registerActivityLifeCycleCallbacks(application: Application) {

        setApplication(application)

        application.registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks)
    }

    private fun setApplication(application: Application) {

        mApplication = application
    }

    companion object {

        private var instance: Injectable? = null

        fun shPrefUtils() : ShPrefUtils {

            return instance!!.shPrefUtils
        }

        fun currentActivity(): Activity? {

            return instance!!.mActivityLifecycleCallbacks.currentActivity
        }

        fun applicationContext() : Context {
            return instance!!.mApplication
        }
    }
}