package com.rzahr.quicktools

import android.app.Activity
import android.app.Application
import android.content.Context
import javax.inject.Inject


@Suppress("unused")
class Injectable @Inject constructor(var shPrefUtils: ShPrefUtils, val codeThrottle: CodeThrottle) {

    val mQuickActivityLifecycleCallbacks = QuickActivityLifeCycleCallbacks()
    lateinit var mApplication: Application

    init { instance = this }

    fun registerActivityLifeCycleCallbacks(application: Application) {

        setApplication(application)

        application.registerActivityLifecycleCallbacks(mQuickActivityLifecycleCallbacks)
    }

    private fun setApplication(application: Application) {

        mApplication = application
    }

    companion object {

        private var instance: Injectable? = null

        fun shPrefUtils() : ShPrefUtils {

            return instance!!.shPrefUtils
        }

        fun codeThrottle() : CodeThrottle {

            return instance!!.codeThrottle
        }

        fun currentActivity(): Activity? {

            return instance!!.mQuickActivityLifecycleCallbacks.currentActivity
        }

        fun applicationContext() : Context {
            return instance!!.mApplication
        }
    }
}