package com.rzahr.quicktools

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Scope

@Scope
@Retention(value = AnnotationRetention.RUNTIME)
annotation class ApplicationScope

@Suppress("unused", "MemberVisibilityCanBePrivate")
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