@file:Suppress("MemberVisibilityCanBePrivate")

package com.rzahr.quicktools.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.webkit.MimeTypeMap
import com.rzahr.quicktools.QuickInjectable
import com.rzahr.quicktools.QuickLogWriter
import com.rzahr.quicktools.R
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.lang.Exception

@Suppress("unused")
object QuickFileUtils {

    /**
     * gets the file mip-map type
     * @return the file mipmap
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
     * delete a file
     * @param path: the path of the file to be deleted
     */
    fun deleteFile(path: String) {

        val fileToDelete = File(path)

        if (fileToDelete.exists()) fileToDelete.delete()
    }

    /**
     * delete a complete directory
     * @param: the path of the directory to be deleted
     */
    fun deleteDirectory(path: String) {

        val directoryToDelete = File(path)

        if (directoryToDelete.exists() && directoryToDelete.isDirectory) {

            if (directoryToDelete.list().isEmpty()) directoryToDelete.delete()

            else {

                for (file in directoryToDelete.list()) deleteDirectory(directoryToDelete.path + "/" + file)

                if (directoryToDelete.list().isEmpty()) directoryToDelete.delete()
            }
        }

        else if (directoryToDelete.exists()) directoryToDelete.delete()
    }

    /**
     * Create directory.
     * @param path        the path
     * @param withNoMedia the with no media
     * @return the state if the directory was created or not
     */
    fun createDirectory(path: String, withNoMedia: Boolean): String {

        val folder = File(path)
        var success = true
        if (!folder.exists()) success = folder.mkdir()

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

    /**
     * get the bitmap from the image file
     * @param f: the file name
     * @param width: the width needed
     * @param height: the height needed
     * @return a bitmap file
     */
    fun decodeFile(f: String, width: Int, height: Int): Bitmap? {

        try {

            //decode image size
            val o = BitmapFactory.Options()
            o.inJustDecodeBounds = true
            BitmapFactory.decodeStream(FileInputStream(f), null, o)
            var scale = 1
            while (o.outWidth / scale / 2 >= width && o.outHeight / scale / 2 >= height)
                scale *= 2

            //decode with inSampleSize
            val o2 = BitmapFactory.Options()
            o2.inSampleSize = scale
            return BitmapFactory.decodeStream(FileInputStream(f), null, o2)
        } catch (e: FileNotFoundException) {
        }

        return null
    }

    /**
     * get a resized bitmap
     * @param bitmap: the bitmap original file
     * @param newHeight: the new height
     * @param newWidth: the new width
     * @param rotation: the rotation
     * @return a resized bitmap file
     */
    @Throws(Exception::class)
    fun getResizedBitmap(bitmap: Bitmap?, newHeight: Int, newWidth: Int, rotation: Int): Bitmap {

        val width = bitmap!!.width
        val height = bitmap.height
        newWidth.toFloat() / width
        newHeight.toFloat() / height
        val matrix = Matrix()
        matrix.setRotate(rotation.toFloat())
        return  Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false)
    }
}