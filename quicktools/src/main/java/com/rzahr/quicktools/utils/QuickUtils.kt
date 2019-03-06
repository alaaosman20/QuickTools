@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.rzahr.quicktools.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.rzahr.quicktools.QuickInjectable
import com.rzahr.quicktools.QuickLogWriter
import com.rzahr.quicktools.R
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
     * safe close buffered writer.
     * @param bufferedWriter the buffered writer
     */
    fun safeCloseBufferedWriter(bufferedWriter: BufferedWriter?) {

        if (bufferedWriter != null) {

            bufferedWriter.flush()
            bufferedWriter.close()
        }
    }

    /**
     * remove illegal sql characters
     * @param value the value
     *
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
     * uses rxjava to perform a background task
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
     * uses rxjava to perform a background task
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
     * changes the time to string
     */
    fun changeTimeToString(lastLocationDate: Long, format: String = "dd/MM/yyyy hh:mm:ss a"): String {

        return SimpleDateFormat(format, Locale.ENGLISH).format(java.util.Date(lastLocationDate).time)
    }

    /**
     * returns the file mipmap
     */
    fun getFileMipMap(fileURi: Uri): String {

        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(fileURi.toString()))

        if (mimeType == null) {

            val mimRegex = regEx("[.].*", fileURi.toString())

            return if (mimRegex.isNotEmpty()) "application/$mimRegex" else ""
        }
        return mimeType.toLowerCase()
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
     * delete file
     */
    fun deleteFile(path: String) {
        val fileToDelete = File(path)

        if (fileToDelete.exists()) fileToDelete.delete()
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

    /**
     * delete a complete directory
     */
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
     * @param withNoMedia the with no media
     */
    fun createDirectory(path: String, withNoMedia: Boolean): String {

        val folder = File(path)
        var success = true
        if (!folder.exists())
            success = folder.mkdir()

        if (success) {

            val noMedia = File(path + "/" + QuickInjectable.applicationContext().resources.getString(
                R.string.NO_MEDIA
            ))

            if (!noMedia.exists() && withNoMedia) {
                try {
                    noMedia.createNewFile()
                } catch (e: IOException) {
                    QuickLogWriter.printStackTrace(e)
                }
            }

            return "Success"
        }

        else return "Failure"
    }
}