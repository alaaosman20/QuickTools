@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.rzahr.quicktools

import android.annotation.SuppressLint
import android.os.Environment
import android.util.Log
import com.rzahr.quicktools.utils.QuickUtils
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object QuickLogWriter {

   const val TAG = "QuickTools_LogWriter"

    /**
     * get caller class string [ ].
     * @param level the level
     * @return the string [ ]
     */
    fun getCallerClass(level: Int): Array<String> {
        return try {
            val stElements = Thread.currentThread().stackTrace
            arrayOf(
                stElements[level + 1].lineNumber.toString() + "",
                stElements[level + 1].fileName,
                stElements[level + 1].methodName
            )
        } catch (e: Exception) {
            arrayOf("", "", "")
        }
    }

    fun logErrorHelper(callingMethod: Array<String>, msg: String, logFileNameTemp: String, error: String, folderName: String, deleteFileIfExist: Boolean) {

        Log.e(callingMethod[1] + " (" + callingMethod[0] + ")", "Method: " + callingMethod[2] + " Msg: " + msg + " //**//Error: " + error)
        appendContents(QuickInjectable.quickPref().get(logFileNameTemp) + ".txt", "Activity: " + callingMethod[1] + "         Method: " + callingMethod[2] + " Line Number: " + callingMethod[0] + " Msg: " + msg + " //**//Error: " + error + " \n",true, folderName, deleteFileIfExist)
    }

    fun logHelper(callingMethod: Array<String>, msg: String, logFileNameTemp: String, error: String, folderName: String, deleteFileIfExist: Boolean) {

        Log.d(callingMethod[1] + " (" + callingMethod[0] + ")", "Method: " + callingMethod[2] + " Msg: " + msg)
        appendContents("$logFileNameTemp.txt", "Activity: " + callingMethod[1] + "         Method: " + callingMethod[2] + " Line Number: " + callingMethod[0] + " Msg: " + msg +error+ " \n",true, folderName, deleteFileIfExist)
    }

    fun logFromSourceClass(logName: String, message: String, deleteFileIfExist: Boolean, folderName: String) {

        try {

            val callingMethod = getCallerClass(3)
            appendContents(
                QuickInjectable.quickPref().get(logName) + ".txt",
                "Activity: " + callingMethod[1] + "         Method: " + callingMethod[2] + " Line Number: " + callingMethod[0] + " Msg: " + message + " \n"
                ,true, folderName, deleteFileIfExist)
        } catch (ignored: Exception) {
        }
    }


    /**
     * Print stack trace.
     * @param e the e
     */
    fun printStackTrace(e: Exception) {
        e.printStackTrace()
    }

    fun debugLogging(message: Any) {

        val callingMethod = getCallerClass(3)
        Log.d(callingMethod[1] + " (" + callingMethod[0] + ")", "Method: " + callingMethod[2] + " Msg: " + message)
    }

    @Synchronized
    fun appendContents(sFileName: String, sContent: String, includeDate: Boolean, folderName: String, deleteFileIfExist: Boolean) {

        try {
            val filePath = Environment.getExternalStorageDirectory().toString() + "/" + folderName + "/" + sFileName
            val oFile = File(filePath)

            if (deleteFileIfExist && oFile.exists()) oFile.delete()

            if (!oFile.exists()) oFile.createNewFile()

            if (oFile.canWrite()) {
                val oWriter = BufferedWriter(FileWriter(File(filePath), true))
                try {
                    oWriter.newLine()
                    if (includeDate) oWriter.write(" ###" + getTodayDateAndTime() + ":" + sContent + " \n\r")
                    else oWriter.write(sContent)
                } finally {
                    QuickUtils.safeCloseBufferedWriter(oWriter)
                }
            }
        } catch (oException: IOException) {
            Log.e(TAG, "Error in appendContents oException $oException")
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getTodayDateAndTime(): String {

        return try {
            val now = Date()
            SimpleDateFormat("dd/MM/yyyy hh:mm:ss a", Locale.ENGLISH).format(now)
        }
        catch (exx: Exception) {
            Log.e(TAG, "Error in GetTodayDateAndTime:$exx")
            ""
        }
    }


    /**
     * Error logging.
     *
     * @param message   the msg
     * @param error the error
     */
    fun errorLogging(message: Any, error: Any) {
        try {
            val callingMethod = getCallerClass(4)
            Log.e(
                callingMethod[1] + " (" + callingMethod[0] + ")",
                "Method: " + callingMethod[2] + " Msg: " + message + " //**//Error: " + error
            )
        }
        catch (ignored: java.lang.Exception){}
    }

}