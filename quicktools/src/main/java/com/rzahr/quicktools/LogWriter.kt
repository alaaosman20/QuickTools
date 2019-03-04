package com.rzahr.quicktools

import android.util.Log

object LogWriter {


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