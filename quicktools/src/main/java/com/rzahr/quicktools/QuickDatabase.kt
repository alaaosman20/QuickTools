package com.rzahr.quicktools

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import javax.inject.Inject

class QuickDatabase @Inject constructor(val context: Context) {

    private var myDataBase: SQLiteDatabase? = null

    private fun getDatabase(): SQLiteDatabase? {

        return getDatabaseInstance(context)
    }

    private fun getDatabaseInstance(context: Context): SQLiteDatabase? {

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

    private fun getDBPath(context: Context): String {

        return context.applicationInfo.dataDir + "/databases/"
    }

    fun closeDatabase() {

        // my database variable is not always NOT NULL and this is because we are reusing this function for multiple purposes
        // first purpose: we want to initialize the database and make it available to read and write from
        // second purpose: we want to use the database to read and write to without having to open it each time we want to use it
        if (myDataBase != null && myDataBase!!.isOpen) myDataBase!!.close()
    }
}