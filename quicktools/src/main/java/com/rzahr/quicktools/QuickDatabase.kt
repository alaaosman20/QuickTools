package com.rzahr.quicktools

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.core.util.LogWriter
import java.lang.Exception
import javax.inject.Inject

@Suppress("unused", "MemberVisibilityCanBePrivate")
abstract class QuickDatabase @Inject constructor(val context: Context) {

    var myDataBase: SQLiteDatabase? = null

    fun getDatabase(): SQLiteDatabase? {

        return getDatabaseInstance(context)
    }

    fun getDatabaseInstance(context: Context): SQLiteDatabase? {

        if (myDataBase != null && myDataBase!!.isOpen) return myDataBase as SQLiteDatabase

        return try {
            myDataBase = SQLiteDatabase.openDatabase(
                getDBPath(context) + "Database.db",
                null,
                SQLiteDatabase.OPEN_READWRITE
            )

            myDataBase!!
        } catch (e: java.lang.Exception) {

            null
        }
    }


    fun getDBPath(context: Context): String {

        return context.applicationInfo.dataDir + "/databases/"
    }

    fun closeDatabase() {

        // my database variable is not always NOT NULL and this is because we are reusing this function for multiple purposes
        // first purpose: we want to initialize the database and make it available to read and write from
        // second purpose: we want to use the database to read and write to without having to open it each time we want to use it
        if (myDataBase != null && myDataBase!!.isOpen) myDataBase!!.close()
    }

    @Throws(Exception::class)
    fun singleSelect(query: String, increment: Boolean, delimiter: String, defaultReturn: String, args: Array<String> = emptyArray()): String {

        getDatabase()

        var returnedValue = defaultReturn
        val callingMethod = QuickLogWriter.getCallerClass(3)
        val cursor: Cursor? = myDataBase!!.rawQuery(query, args)

        if (cursor != null && cursor.moveToFirst()) {

            do {

                if (increment) returnedValue += cursor.getString(0) + delimiter

                else returnedValue = if (cursor.getString(0) == null) defaultReturn else cursor.getString(0)

            } while (cursor.moveToNext())
        }

        cursor?.close()

        QuickLogWriter.debugLogging("Class: " + callingMethod[1] + " (" + callingMethod[0] + ") Method: " + callingMethod[2] + " Result Is : " + returnedValue)

        return returnedValue
    }
}
