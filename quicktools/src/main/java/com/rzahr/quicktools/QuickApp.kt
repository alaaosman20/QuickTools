package com.rzahr.quicktools

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.BatteryManager
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import com.rzahr.quicktools.extensions.addWithId
import com.rzahr.quicktools.utils.QuickUtils
import java.util.*

@Suppress("unused")
object QuickApp {

    /**
     * used in base classes to change language on demand
     */
    fun getWrapper(newBase: Context?): ContextWrapper? {

        return QuickContextWrapper.wrap(newBase, QuickInjectable.quickPref().get("Language"))
    }

    /**
     * checks if the device is connected to a wifi or 3g
     * @return Boolean value
     */
    @SuppressLint("MissingPermission")
    fun isOnline(): Boolean {

        val networkInfo = (QuickInjectable.applicationContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    /**
     * returns the battery level
     */
    fun getBatteryLevel(): Int {

        var battery: Int

        try {

            val batteryLevel: Int
            val batteryScale: Int

            val batteryIntent = QuickInjectable.applicationContext().registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

            batteryLevel = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1

            batteryScale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1

            // Error checking that probably isn't needed but I added just in case.
            if (batteryLevel == -1 || batteryScale == -1) {
                battery = Math.round(50f)
                return battery
            }

            battery = Math.round(batteryLevel.toFloat() / batteryScale.toFloat() * 100f)

            return battery
        }

        catch (exc: Exception) {

            QuickLogWriter.errorLogging("Error in getBatteryLevel:", exc.toString())

            battery = 0

            return battery
        }

    }

    /**
     * boolean value representing if the device is plugged in or not
     */
    fun isPluggedIn(): Boolean {

        try {

            val plugged = QuickInjectable.applicationContext().registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))?.getIntExtra(
                BatteryManager.EXTRA_PLUGGED, -1) ?: -1

            return plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB || plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS
        }

        catch (exc: Exception) {

            QuickLogWriter.errorLogging("Error in isPluggedIn:", exc.toString())
        }

        return false
    }

    /**
     * returns the current language symbol
     */
    fun getLanguageIdentifier(): String {

        @Suppress("DEPRECATION") val locale: Locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            QuickInjectable.applicationContext().resources.configuration.locales.get(0)
        else QuickInjectable.applicationContext().resources.configuration.locale

        val lang: String
        lang = if (locale.toString() == "l" || locale.toString() == "en_US" || locale.toString().contains("en", true))
            QuickVariables.ENGLISH_LANG_KEY
        else QuickVariables.ARABIC_LANG_KEY

        return lang
    }

    /**
     * boolean value identifying if the application is white-listed
     */
    fun isInDozeWhiteList(): Boolean? {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true
        return QuickInjectable.applicationContext().getSystemService(PowerManager::class.java).isIgnoringBatteryOptimizations(QuickInjectable.applicationContext().packageName)
    }

    /**
     * boolean value representing if the power saver is enabled
     */
    fun isPowerSaverOn(): Boolean {

        val powerManager = QuickInjectable.applicationContext().getSystemService(Context.POWER_SERVICE) as PowerManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && powerManager.isPowerSaveMode) {
            return true
        }
        return false
    }

    /**
     * boolean value representing if the screen is turned on
     */
    fun isScreenOn(): Boolean {

        val powerManager = QuickInjectable.applicationContext().getSystemService(Context.POWER_SERVICE) as PowerManager
        @Suppress("DEPRECATION")
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH &&
                powerManager.isInteractive || Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT_WATCH &&
                powerManager.isScreenOn
    }

    /**
     * returns the device name
     */
    fun getDeviceName(): String {

        return try {
            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            if (model.startsWith(manufacturer)) {
                model
            } else {
                "$manufacturer $model"
            }
        } catch (ex: Exception) {
            "UNKNOWN"
        }
    }

    /**
     * returns the operating system name
     */
    fun getOSName(): String {

        return try {
            Build.VERSION.RELEASE
        } catch (e: Exception) {
            "Not Found"
        }
    }

    /**
     * returns the unique identifier
     */
    @SuppressLint("HardwareIds")
    fun getUUID(): String {

        if (QuickVariables.UUID == "")
            QuickVariables.UUID = Settings.Secure.getString(
                QuickInjectable.applicationContext().contentResolver,
                Settings.Secure.ANDROID_ID
            )
        return QuickVariables.UUID
    }

    /**
     * checks the current application version name
     * @param directRequest used to either check directly the version name or get the value stored in the shared preference
     * @return String value
     */
    fun getVersionName(directRequest: Boolean): String {

        if (directRequest) {
            return try {
                val version = QuickInjectable.applicationContext()
                    .packageManager.getPackageInfo(QuickInjectable.applicationContext().packageName, 0).versionName
                version.addWithId(QuickVariables.VERSION_NAME)
                version
            } catch (e: Exception) {
                ""
            }
        }
        return QuickInjectable.quickPref().get(QuickVariables.VERSION_NAME)
    }

    fun backgrounded(): Boolean {

        var isInBackground = true
        var tasksList: List<*>? = null
        val activityManager = QuickInjectable.applicationContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        if (Build.VERSION.SDK_INT > 20) {
            tasksList = activityManager.runningAppProcesses

        } else {
            try {
                @Suppress("DEPRECATION")
                tasksList = activityManager.getRunningTasks(1)

            } catch (ignored: Exception) {
            }

        }
        if (tasksList != null && tasksList.isNotEmpty()) {
            when {
                Build.VERSION.SDK_INT > 22 -> {
                    val runningProcesses = activityManager.runningAppProcesses
                    for (processInfo in runningProcesses) {
                        if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                            for (activeProcess in processInfo.pkgList) {
                                if (activeProcess == QuickInjectable.applicationContext().packageName) {
                                    isInBackground = false
                                }
                            }
                        }
                    }
                    return isInBackground
                }
                Build.VERSION.SDK_INT > 20 -> {
                    val packageName = activityManager.runningAppProcesses[0].processName
                    return packageName != QuickInjectable.applicationContext().packageName
                }
                else -> {
                    @Suppress("DEPRECATION") val topActivity = activityManager.getRunningTasks(1)[0].topActivity
                    return topActivity.packageName != QuickInjectable.applicationContext().packageName
                }
            }
        } else {
            return false
        }
    }

    fun isNetworkAvailable(): Boolean {

        try {
            val isOnline = isOnline()

            if (isOnline) {

                if (QuickInjectable.quickPref().get(QuickInternetCheckService.ONLINE_SINCE_KEY) == "") QuickUtils.getCurrentDate(true).addWithId(QuickInternetCheckService.ONLINE_SINCE_KEY)

                "".addWithId(QuickInternetCheckService.OFFLINE_SINCE_KEY)
            }

            else {

                if (QuickInjectable.quickPref().get(QuickInternetCheckService.OFFLINE_SINCE_KEY) == "") QuickUtils.getCurrentDate(true).addWithId(QuickInternetCheckService.OFFLINE_SINCE_KEY)

                "".addWithId(QuickInternetCheckService.ONLINE_SINCE_KEY)
            }

            return isOnline

        } catch (e: Exception) {
            return false
        }

    }
}