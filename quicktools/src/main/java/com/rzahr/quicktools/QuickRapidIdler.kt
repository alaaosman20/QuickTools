package com.rzahr.quicktools

import javax.inject.Inject

/**
 * prevents multi rapid clicks
 */
class QuickRapidIdler @Inject constructor() {
    companion object {
        const val MIN_INTERVAL = 800
    }
    private var lastEventTime = System.currentTimeMillis()
    private var initialized = false

    fun throttle(code: () -> Unit) {
        val eventTime = System.currentTimeMillis()
        if (eventTime - lastEventTime > MIN_INTERVAL || !initialized) {
            initialized = true
            lastEventTime = eventTime
            code()
        }
    }
}
