package com.rzahr.quicktools.utils

import android.net.Uri
import android.webkit.MimeTypeMap
import com.rzahr.quicktools.QuickInjectable
import com.rzahr.quicktools.QuickLogWriter
import com.rzahr.quicktools.R
import java.io.File
import java.io.IOException

@Suppress("unused")
object QuickFileUtils {

    /**
     * returns the file mipmap
     */
    fun getFileMipMap(fileURi: Uri): String {

        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(fileURi.toString()))

        if (mimeType == null) {

            val mimRegex = QuickUtils.regEx("[.].*", fileURi.toString())

            return if (mimRegex.isNotEmpty()) "application/$mimRegex" else ""
        }
        return mimeType.toLowerCase()
    }

    /**
     * delete file
     */
    fun deleteFile(path: String) {
        val fileToDelete = File(path)

        if (fileToDelete.exists()) fileToDelete.delete()
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

            val noMedia = File(
                "$path/" + QuickInjectable.applicationContext().resources.getString(
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