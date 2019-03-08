@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.rzahr.quicktools.utils

import android.app.Activity
import android.app.ActivityManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.util.LogWriter
import com.rzahr.quicktools.QuickInjectable
import com.rzahr.quicktools.QuickLogWriter
import io.reactivex.Scheduler
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

object QuickUtils {

    /**
     * remove illegal sql characters
     * @param value the value
     * @return pure value
     */
    fun removeIllegalSQLChars(value: String): String {

        @Suppress("NAME_SHADOWING") var value = value
        value = value.replace("'", " ")
        value = value.replace("&", "")
        value = value.replace("#", "")
        value = value.replace("\"", "")
        value = value.replace("^", "")

        return value
    }

    /**
     * rounds a number
     */
    fun roundNumber(value: Double, places: Int): Double {

        @Suppress("NAME_SHADOWING") var value = value
        if (places < 0) throw IllegalArgumentException()
        val factor = Math.pow(10.0, places.toDouble()).toLong()
        value *= factor
        val tmp = Math.round(value)
        return tmp.toDouble() / factor
    }

    /**
     * uses rx java to perform a background task
     */
    fun backgroundUpdater(backgroundFunction: () -> Any?, ForegroundFunction: (it: Any?) -> Unit, ErrorFunction: (it: Throwable) -> Unit?) {

        Single.fromCallable { backgroundFunction() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doAfterSuccess { ForegroundFunction(it) }
            .doOnError{ ErrorFunction(it) }
            .subscribe()
    }

    /**
     * uses rx java to perform a background task
     */
    class RZBackgroundUpdater<T> constructor (backgroundFunction: () -> T?, foregroundFunction: (it: T?) -> Unit, errorFunction: (it: Throwable) -> Unit?, subscribeOn: Scheduler = Schedulers.io(), observeOn: Scheduler = AndroidSchedulers.mainThread()) {

        init {
            Single.fromCallable { backgroundFunction() }
                .subscribeOn(subscribeOn)
                .observeOn(observeOn)
                .doAfterSuccess { foregroundFunction(it) }
                .doOnError { errorFunction(it) }
                .subscribe()
        }
    }

    fun cancelPendingNotifications(notificationId: String) {

        try {

            (QuickInjectable.applicationContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(notificationId.hashCode())
        }

        catch (e:Exception) { }
    }

    private fun canHandleIntent(intent: Intent, activity: Activity): Boolean {

        return activity.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size > 0
    }

    /**
     * opens google map application if available
     */
    fun openGoogleMapsApp(uri: String, activity: Activity) {

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity")

        if (canHandleIntent(intent, activity))  activity.startActivity(intent)

        else Toast.makeText(activity.applicationContext, "Please Install Google Navigation", Toast.LENGTH_LONG).show()
    }

    /**
     * regular expression search
     */
    fun regEx(patternString: String, word: String): String {

        val matcher =  Pattern.compile(patternString).matcher(word)
        var matchedString = ""

        while (matcher.find()) matchedString = matcher.group()

        return matchedString
    }

    /**
     * string to html
     */
    fun fromHtml(html: String): Spanned {

        @Suppress("DEPRECATION")
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY) else Html.fromHtml(html)
    }

    /**
     * request a certain permission
     */
    fun requestPermission(currentActivity: Activity, manifestPermission: String, permissionIdentifier: Int) {

        ActivityCompat.requestPermissions(
            currentActivity,
            arrayOf(manifestPermission),
            permissionIdentifier
        )
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
     * safe close buffered writer.
     * @param bufferedWriter the buffered writer
     */
    fun safeCloseBufferedWriter(bufferedWriter: BufferedWriter?) {

        if (bufferedWriter != null) {

            bufferedWriter.flush()
            bufferedWriter.close()
        }
    }

    @Throws(Exception::class)
    private fun isMyServiceRunning(serviceClass: Class<*>, context: Context): Boolean {

            @Suppress("DEPRECATION")
            for (service in (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).getRunningServices(
                Integer.MAX_VALUE
            ))
                if (serviceClass.name.toLowerCase(Locale.ENGLISH).contains(
                        service.service.className.toLowerCase(
                            Locale.ENGLISH
                        )
                    )
                ) return true

        return false
    }

    /**
     * gets the current date in a specific format
     */
    fun getDate(): String {
        return try {
            SimpleDateFormat("dd/MM/yyyy hh:mm:ss a", Locale.ENGLISH).format(java.util.Date())
        } catch (exx: Exception) {
            QuickLogWriter.errorLogging("Error in getTodayDateAndTime:", exx.toString())
            ""
        }
    }

    fun changeTimeToGMT(lastLocationDate: Long): String {

        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm:ss a", Locale.ENGLISH)
        simpleDateFormat.timeZone = TimeZone.getTimeZone("GMT")
        return simpleDateFormat.format(java.util.Date(lastLocationDate).time)
    }
}