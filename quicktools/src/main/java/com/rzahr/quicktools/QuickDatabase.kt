package com.rzahr.quicktools

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import java.util.*
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
    open fun singleSelect(query: String, increment: Boolean, delimiter: String, defaultReturn: String, args: Array<String> = emptyArray()): String {

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

    @Throws(Exception::class)
    open fun multiSelectNonCapital(query: String, onResult: (columnName: String, columnValue: String) -> Unit) {

        getDatabase()
        val callingMethod = QuickLogWriter.getCallerClass(3)

        val cursor = myDataBase?.rawQuery(query, null)

        if (cursor!!.moveToFirst()) {

            do for (i in 0 until cursor.columnCount) onResult(cursor.getColumnName(i), cursor.getString(i))

            while (cursor.moveToNext())
        }

        cursor.close()

        QuickLogWriter.debugLogging("Class: " + callingMethod[1] + " (" + callingMethod[0] + ") Method: " + callingMethod[2])
    }

    @Throws(Exception::class)
    open fun multiSelect(query: String, onResult: (cursor: Cursor) -> Unit) {

        getDatabase()
        val callingMethod = QuickLogWriter.getCallerClass(3)
        val cursor = myDataBase?.rawQuery(query, null)

        if (cursor!!.moveToFirst()) {

            do onResult(cursor)

            while (cursor.moveToNext())
        }

        cursor.close()

        QuickLogWriter.debugLogging("Class: " + callingMethod[1] + " (" + callingMethod[0] + ") Method: " + callingMethod[2])
    }

    @Throws(Exception::class)
    open fun multiSelect(query: String, @Suppress("UNUSED_PARAMETER") any: Any): ArrayList<HashMap<String, String>> {

        getDatabase()

        val callingMethod = QuickLogWriter.getCallerClass(3)
        val sqlDataResultArray = ArrayList<HashMap<String, String>>()
        val cursor = myDataBase?.rawQuery(query, null)

        if (cursor!!.moveToFirst()) {

            do {

                val temp: LinkedHashMap<String, String> = LinkedHashMap()

                for (i in 0 until cursor.columnCount) {

                    var value = cursor.getString(i)
                    if (value == null) value = ""
                    temp[cursor.columnNames[i].toUpperCase(Locale.ENGLISH)] = value
                }

                sqlDataResultArray.add(temp)
            }

            while (cursor.moveToNext())
        }

        cursor.close()

        QuickLogWriter.debugLogging("Class: " + callingMethod[1] + " (" + callingMethod[0] + ") Method: " + callingMethod[2] + " Result Is : " + sqlDataResultArray)

        return sqlDataResultArray
    }

    @Throws(Exception::class)
    open fun multiSelect(query: String): ArrayList<Array<String>> {

        getDatabase()

        val callingMethod = QuickLogWriter.getCallerClass(3)
        val sqlDataResultArray = ArrayList<Array<String>>()
        var returnString = ""
        var temp: Array<String>

        val cursor = myDataBase?.rawQuery(query, null)

        if (cursor!!.moveToFirst()) {

            do {

                for (i in 0 until cursor.columnCount) returnString += cursor.getString(i) + "#RZZZ&%#"

                temp = returnString.split("#RZZZ&%#".toRegex()).toTypedArray()
                sqlDataResultArray.add(temp)
                returnString = ""

            } while (cursor.moveToNext())
        }

        cursor.close()

        QuickLogWriter.debugLogging("Class: " + callingMethod[1] + " (" + callingMethod[0] + ") Method: " + callingMethod[2] + " Result Is : " + sqlDataResultArray)

        return sqlDataResultArray
    }
}