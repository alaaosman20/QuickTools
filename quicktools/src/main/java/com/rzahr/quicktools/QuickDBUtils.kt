package com.rzahr.quicktools

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*

object QuickDBUtils {

    fun simpleSelect(columns: String, table: String, whereClause: String = "", groupByClause: String = "", orderByClause: String = ""): String {

        var query = "SELECT $columns FROM $table"

        if (whereClause.isNotEmpty()) query += " WHERE $whereClause"

        if (groupByClause.isNotEmpty()) query+= " GROUP BY $groupByClause"

        if (orderByClause.isNotEmpty()) query+= " ORDER BY $orderByClause"

        QuickLogWriter.debugLogging(query)

        return query
    }

    fun complexSelect(columns: String, table: String, joins: Array<String>, whereClause: String = "", groupByClause: String = ""): String {

        var query = "SELECT $columns FROM $table "

        for (join in joins) query += "LEFT OUTER JOIN $join "

        if (whereClause.isNotEmpty()) query+= "WHERE $whereClause"

        if (groupByClause.isNotEmpty()) query+= "GROUP BY $groupByClause"

        QuickLogWriter.debugLogging(query)

        return query
    }

    fun distinctSelect(columns: String, table: String, whereClause: String = ""): String {

        var query = "SELECT DISTINCT $columns FROM $table"

        if (whereClause.isNotEmpty()) query += " WHERE $whereClause"

        QuickLogWriter.debugLogging(query)

        return query
    }

    /**
     * boolean value representing if the database exist
     */
    fun databaseExist(): Boolean {

        val dbName = "Database.db"
        val dbFile = QuickInjectable.applicationContext().getDatabasePath(dbName)
        return if (dbFile.exists())
            true
        else {
            if (!Arrays.asList(*QuickInjectable.applicationContext().assets.list("")).contains(dbName))
                return false

            QuickUtils.createDirectory(
                getDBPath(),  false
            )

            val inputStream = QuickInjectable.applicationContext().assets.open(dbName)
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

    /**
     * copies the database to the internal application directory
     */
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
     */
    fun getDBPath(): String {

        return QuickInjectable.applicationContext().applicationInfo.dataDir + "/databases/"
    }
}