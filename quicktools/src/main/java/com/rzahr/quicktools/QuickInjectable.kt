package com.rzahr.quicktools

import android.app.Activity
import android.app.Application
import android.content.Context
import javax.inject.Inject


@Suppress("unused")
class QuickInjectable @Inject constructor(var quickPref: QuickPref, val quickRapidIdler: QuickRapidIdler) {

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

        private var instance: QuickInjectable? = null

        fun quickPref() : QuickPref {

            return instance!!.quickPref
        }

        fun codeThrottle() : QuickRapidIdler {

            return instance!!.quickRapidIdler
        }

        fun currentActivity(): Activity? {

            return instance!!.mQuickActivityLifecycleCallbacks.currentActivity
        }

        fun applicationContext() : Context {
            return instance!!.mApplication
        }
    }
}