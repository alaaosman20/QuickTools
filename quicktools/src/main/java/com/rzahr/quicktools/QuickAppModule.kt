package com.rzahr.quicktools

import android.annotation.SuppressLint
import android.app.Application
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Scope

@Scope
@Retention(value = AnnotationRetention.RUNTIME)
annotation class ApplicationScope

@Module
class QuickAppModule constructor(val context: Context, @Suppress("MemberVisibilityCanBePrivate") val rapidIdler: QuickClickGuard, val application: Application) {

    @Provides
    @ApplicationScope
    fun app(): Application {

        return application
    }

    @Provides
    @ApplicationScope
    fun rapidIdler(): QuickClickGuard {

        return rapidIdler
    }

    @Provides
    @ApplicationScope
    fun applicationContext(): Context {

        return context
    }
}