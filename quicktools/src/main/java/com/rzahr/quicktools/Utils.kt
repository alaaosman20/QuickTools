package com.rzahr.quicktools

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.BatteryManager
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.text.Html
import android.text.Spanned
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.rzahr.quicktools.QuickVariables.ARABIC_LANG_KEY
import com.rzahr.quicktools.QuickVariables.ENGLISH_LANG_KEY
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.w3c.dom.Document
import org.xml.sax.InputSource
import org.xml.sax.SAXException
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException

object QuickVariables {
    var UUID = ""
    const val VERSION_NAME: String = "versionname"
    const val ARABIC_LANG_KEY = "A"
    const val ENGLISH_LANG_KEY = "L"
}

object QuickApp {

    /**7
     * checks if the device is connected to a wifi or 3g
     * @return Boolean value
     */
    @SuppressLint("MissingPermission")
    fun isOnline(): Boolean {

        val networkInfo = (Injectable.applicationContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    /**
     * Get current custom date string.
     * @return the string
     */


    fun getBatteryLevel(): Int {

        var battery: Int

        try {

            val batteryLevel: Int
            val batteryScale: Int

            val batteryIntent = Injectable.applicationContext().registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

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

            LogWriter.errorLogging("Error in getBatteryLevel:", exc.toString())

            battery = 0

            return battery
        }

    }

    fun isPluggedIn(): Boolean {

        try {

            val plugged = Injectable.applicationContext().registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))?.getIntExtra(
                BatteryManager.EXTRA_PLUGGED, -1) ?: -1

            return plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB || plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS
        }

        catch (exc: Exception) {

            LogWriter.errorLogging("Error in isPluggedIn:", exc.toString())
        }

        return false
    }

    /**
     * returns the current language symbol
     */
    fun getLanguageIdentifier(): String {

        @Suppress("DEPRECATION") val locale: Locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            Injectable.applicationContext().resources.configuration.locales.get(0)
        else Injectable.applicationContext().resources.configuration.locale

        val lang: String
        lang = if (locale.toString() == "l" || locale.toString() == "en_US" || locale.toString().contains("en", true))
            ENGLISH_LANG_KEY
        else ARABIC_LANG_KEY

        return lang
    }

    fun isInDozeWhiteList(): Boolean? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true
        val powerManager = Injectable.applicationContext().getSystemService(PowerManager::class.java)
        return powerManager.isIgnoringBatteryOptimizations(Injectable.applicationContext().packageName)
    }

    fun isPowerSaverOn(): Boolean {

        val powerManager = Injectable.applicationContext().getSystemService(Context.POWER_SERVICE) as PowerManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && powerManager.isPowerSaveMode) {
            return true
        }
        return false
    }

    fun isScreenOn(): Boolean {

        val powerManager = Injectable.applicationContext().getSystemService(Context.POWER_SERVICE) as PowerManager
        @Suppress("DEPRECATION")
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH &&
                powerManager.isInteractive || Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT_WATCH &&
                powerManager.isScreenOn
    }

    /**
     * checks the device name
     * @return String value
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
     * checks the operating system name
     * @return String value
     */
    fun getOSName(): String {
        return try {
            Build.VERSION.RELEASE
        } catch (e: Exception) {
            "Not Found"
        }
    }

    /**
     * gets the unique identifier used to identify the device
     * @return String value
     */
    @SuppressLint("HardwareIds")
    fun getUUID(): String {
        if (QuickVariables.UUID == "")
            QuickVariables.UUID = Settings.Secure.getString(
                Injectable.applicationContext().contentResolver,
                Settings.Secure.ANDROID_ID
            )
        return QuickVariables.UUID
    }

    /**
     * checks the current application version name
     * @param directRequest used to either check directly the version name or get the value stored in the shared preference
     * @return String value
     */
    fun getVersionName(directRequest: Boolean): String? {
        if (directRequest) {
            return try {
                val version = Injectable.applicationContext()
                    .packageManager.getPackageInfo(Injectable.applicationContext().packageName, 0).versionName
                version.addWithId(QuickVariables.VERSION_NAME)
                version
            } catch (e: Exception) {
                ""
            }
        }
        return Injectable.shPrefUtils().get(QuickVariables.VERSION_NAME)
    }

    fun backgrounded(): Boolean {
        var isInBackground = true
        var tasksList: List<*>? = null
        val activityManager = Injectable.applicationContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

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
                                if (activeProcess == Injectable.applicationContext().packageName) {
                                    isInBackground = false
                                }
                            }
                        }
                    }
                    return isInBackground
                }
                Build.VERSION.SDK_INT > 20 -> {
                    val packageName = activityManager.runningAppProcesses[0].processName
                    return packageName != Injectable.applicationContext().packageName
                }
                else -> {
                    @Suppress("DEPRECATION") val topActivity = activityManager.getRunningTasks(1)[0].topActivity
                    return topActivity.packageName != Injectable.applicationContext().packageName
                }
            }
        } else {
            return false
        }
    }

}

object QuickUtils {

    fun roundNumber(value: Double, places: Int): Double {

        @Suppress("NAME_SHADOWING") var value = value
        if (places < 0) throw IllegalArgumentException()
        val factor = Math.pow(10.0, places.toDouble()).toLong()
        value *= factor
        val tmp = Math.round(value)
        return tmp.toDouble() / factor
    }


    /**
     * converts drawable to bitmap
     *
     * @param drawable the drawable
     * @return the bitmap
     */
    fun drawableToBitmap(drawable: Drawable): Bitmap {

        if (drawable is BitmapDrawable) return drawable.bitmap

        var width = drawable.intrinsicWidth
        width = if (width > 0) width else 96

        var height = drawable.intrinsicHeight
        height = if (height > 0) height else 96

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    fun backgroundUpdater(backgroundFunction: () -> Any?, ForegroundFunction: (it: Any?) -> Unit, ErrorFunction: (it: Throwable) -> Unit) {

        Single.fromCallable { backgroundFunction() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doAfterSuccess { ForegroundFunction(it) }
            .doOnError{ ErrorFunction(it) }
            .subscribe()
    }

    fun cancelPendingNotifications(notificationId: String) {

        try {
            val mNotificationManager = Injectable.applicationContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            mNotificationManager.cancel(notificationId.hashCode())
        }

        catch (e:Exception) { }
    }

    private fun canHandleIntent(intent: Intent, activity: Activity): Boolean {

        return activity.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size > 0
    }

    fun openGoogleMapsApp(uri: String, activity: Activity) {

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity")

        if (canHandleIntent(intent, activity))  activity.startActivity(intent)

        else Toast.makeText(activity.applicationContext, "Please Install Google Navigation", Toast.LENGTH_LONG).show()
    }

    fun changeTimeToString(lastLocationDate: Long, format: String = "dd/MM/yyyy hh:mm:ss a"): String {

        return SimpleDateFormat(format, Locale.ENGLISH).format(java.util.Date(lastLocationDate).time)
    }

    fun getFileMipMap(fileURi: Uri): String {

        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(fileURi.toString()))

        if (mimeType == null) {

            val mimRegex = regEx("[.].*", fileURi.toString())

            return if (mimRegex.isNotEmpty()) "application/$mimRegex" else ""
        }
        return mimeType.toLowerCase()
    }

    fun regEx(patternString: String, word: String): String {

        val matcher =  Pattern.compile(patternString).matcher(word)
        var matchedString = ""

        while (matcher.find()) matchedString = matcher.group()

        return matchedString
    }

    fun getFileURI(file: File): Uri {

        return if (Build.VERSION.SDK_INT >= 24) FileProvider.getUriForFile(Injectable.applicationContext(), Injectable.applicationContext().applicationContext.packageName + ".provider", file) else Uri.fromFile(file)
    }

    fun fromHtml(html: String): Spanned {

        @Suppress("DEPRECATION")
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY) else Html.fromHtml(html)
    }

    fun deleteFile(path: String) {
        val fileToDelete = File(path)

        if (fileToDelete.exists()) fileToDelete.delete()
    }

    fun requestPermission(currentActivity: Activity, manifestPermission: String, permissionIdentifier: Int) {
        ActivityCompat.requestPermissions(
            currentActivity,
            arrayOf(manifestPermission),
            permissionIdentifier
        )
    }

    /**
     * returns a string date
     * @param date the date
     * @param dateFormat the format
     */
    fun getDateString(date: Date, dateFormat: String): String {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return SimpleDateFormat(dateFormat, Locale.ENGLISH).format(calendar.time)
    }

    @SuppressLint("SimpleDateFormat")
    fun getCurrentDate(english: Boolean, format: String = "yyyy-MM-dd HH:mm:ss"): String {
        val now = Date()
        return if (english)
            SimpleDateFormat(format, Locale.ENGLISH).format(now)
        else
            SimpleDateFormat(format).format(now)
    }

    fun deleteDirectory(path: String) {
        val directoryToDelete = File(path)
        if (directoryToDelete.exists() && directoryToDelete.isDirectory) {
            if (directoryToDelete.list().isEmpty())
                directoryToDelete.delete()
            else {
                for (file in directoryToDelete.list())
                    deleteDirectory(directoryToDelete.path + "/" + file)

                if (directoryToDelete.list().isEmpty())
                    directoryToDelete.delete()
            }
        } else if (directoryToDelete.exists())
            directoryToDelete.delete()

    }

    @Throws(ParserConfigurationException::class, IOException::class, SAXException::class)
    fun parseDoc(inputSource: InputSource): Document {

        val documentBuilderFactory = DocumentBuilderFactory.newInstance()
        documentBuilderFactory.isNamespaceAware = true
        val builder = documentBuilderFactory.newDocumentBuilder()

        return builder.parse(inputSource)
    }

    fun convertStreamToString(inputStream: InputStream, newLineSupport: Boolean): String {

        val reader = BufferedReader(InputStreamReader(inputStream))
        val stringBuilder = StringBuilder()
        var line: String? = null
        try {
            while ({ line = reader.readLine(); line }() != null) {
                if (newLineSupport) {
                    stringBuilder.append(line).append('\n')
                } else {
                    stringBuilder.append(line)
                }
            }
        } catch (e: IOException) {
            // printStackTrace(e)
        } finally {
            try {
                inputStream.close()
            } catch (e: IOException) {
                // printStackTrace(e)
            }
        }

        return stringBuilder.toString()
    }

    /**
     * Create directory.
     * @param path        the path
     * @param context     the context
     * @param withNoMedia the with no media
     */
    fun createDirectory(path: String, withNoMedia: Boolean): String {
        val folder = File(path)
        var success = true
        if (!folder.exists())
            success = folder.mkdir()

        if (success) {

            val noMedia = File(path + "/" + Injectable.applicationContext().resources.getString(R.string.NO_MEDIA))

            if (!noMedia.exists() && withNoMedia) {
                try {
                    noMedia.createNewFile()
                } catch (e: IOException) {
                    LogWriter.printStackTrace(e)
                }
            }

            return "Success"
        }

        else return "Failure"
    }

}

object QuickDBUtils {

    fun createSimpleSelect(columns: String, table: String, whereClause: String = "", groupByClause: String = "", orderByClause: String = ""): String {

        var query = "SELECT $columns FROM $table"

        if (whereClause.isNotEmpty()) query += " WHERE $whereClause"

        if (groupByClause.isNotEmpty()) query+= " GROUP BY $groupByClause"

        if (orderByClause.isNotEmpty()) query+= " ORDER BY $orderByClause"

        LogWriter.debugLogging(query)

        return query
    }

    fun createComplexSelect(columns: String, table: String, joins: Array<String>, whereClause: String = "", groupByClause: String = ""): String {

        var query = "SELECT $columns FROM $table "

        for (join in joins) query += "LEFT OUTER JOIN $join "

        if (whereClause.isNotEmpty()) query+= "WHERE $whereClause"

        if (groupByClause.isNotEmpty()) query+= "GROUP BY $groupByClause"

        LogWriter.debugLogging(query)

        return query
    }

    fun createDistinctSelect(columns: String, table: String, whereClause: String = ""): String {

        var query = "SELECT DISTINCT $columns FROM $table"

        if (whereClause.isNotEmpty()) query += " WHERE $whereClause"

        LogWriter.debugLogging(query)

        return query
    }

    /**
     * Database exist boolean.
     * @param context the context
     * @return the boolean
     */
    fun databaseExist(): Boolean {

        val dbName = "Database.db"
        val dbFile = Injectable.applicationContext().getDatabasePath(dbName)
        return if (dbFile.exists())
            true
        else {
            if (!Arrays.asList(*Injectable.applicationContext().assets.list("")).contains(dbName))
                return false

            QuickUtils.createDirectory(
                getDBPath(),  false
            )

            val inputStream = Injectable.applicationContext().assets.open(dbName)
            val out = FileOutputStream(File(getDBPath() + dbName))
            val buf = ByteArray(1024)
            while (inputStream.read(buf) > 0) {
                out.write(buf)
            }

            inputStream.close()
            out.flush()
            out.close()
            true
        }
    }

    fun copyDatabaseFromExternalDirectory(dbExternalPath: String, downloadedDbName: String): Boolean {

        val outFileName = getDBPath() + downloadedDbName
        val fileDirectory = File(File(outFileName).parent)
        if (!fileDirectory.exists()) {
            if (!fileDirectory.mkdirs()) {
                return false
            }
        }
        QuickUtils.createDirectory(
            getDBPath(), false
        )

        val inputStream = FileInputStream(dbExternalPath + downloadedDbName)
        val out = FileOutputStream(File(outFileName))
        val buf = ByteArray(1024)
        while (inputStream.read(buf) > 0) {
            out.write(buf)
        }

        inputStream.close()
        out.flush()
        out.close()

        return true
    }

    /**
     * Get db path string.
     * @param context the context
     * @return the string
     */
    fun getDBPath(): String {

        return Injectable.applicationContext().applicationInfo.dataDir + "/databases/"
    }
}