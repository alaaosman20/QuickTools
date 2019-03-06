@file:Suppress("unused")

package com.rzahr.quicktools

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import java.util.*


/**
 * saves instance state of a string
 */
fun String.saveStateOf(id: String, outState: Bundle?) {

    outState?.putString(id, this)
}

/**
 * saves instance state of a boolean
 */
fun Boolean.saveStateOf(id: String, outState: Bundle?) {

    outState?.putBoolean(id, this)
}

/**
 * saves instance state of a parcelable array list
 */
fun ArrayList<out Parcelable>.saveStateOf(id: String, outState: Bundle?) {

    outState?.putParcelableArrayList(id, this)
}

/**
 * saves instance state of a parcelable
 */
fun Parcelable.saveStateOf(id: String, outState: Bundle?) {

    outState?.putParcelable(id, this)
}

/**
 * gets the saved instance state of a parcelable array list
 */
fun <T : Parcelable> Bundle?.getStateOf(id: String): ArrayList<T>? {

    return this?.getParcelableArrayList(id)
}

/**
 * gets the saved instance state of a parcelable
 * the parcelable attribute is needed to differentiate between this method and the one for the array list of parcelable
 */
fun <T : Parcelable> Bundle?.getStateOf(id: String, @Suppress("UNUSED_PARAMETER") parcelable: Int): T? {

    return this?.getParcelable(id)
}

/**
 * gets the saved instance state of a string
 */
fun Bundle?.getStateOf(id: String, default: String = ""): String {

    return try {

        this?.getString(id, default)!!
    }

    catch (e:Exception){

        default
    }
}

/**
 * gets the saved instance state of a boolean
 */
fun Bundle?.getStateOf(id: String, default: Boolean = false): Boolean {

    return try {

        this?.getBoolean(id, default)!!
    }

    catch (e:Exception){

        default
    }
}

/**
 * saves integer in shared preference
 */
fun Int.addWithId(id: String) {

    QuickInjectable.quickPref().setIntValue(id, this)
}

/**
 * saves string in shared preference
 */
fun String.addWithId(id: String) {

    QuickInjectable.quickPref().setString(id, this)
}


/**
 * saves double in shared preference
 */
fun Double.addWithId(id: String) {

    QuickInjectable.quickPref().setString(id, this.toString())
}

/**
 * saves long in shared preference
 */
fun Long.addWithId(id: String) {

    QuickInjectable.quickPref().setLong(id, this)
}

/**
 * saves boolean in shared preference
 */
fun Boolean.addWithId(id: String) {

    QuickInjectable.quickPref().setBoolean(id, this)
}

/**
 * saves string in shared preference as default
 */
fun String.addAsDefaultWithId(id: String) {

    QuickInjectable.quickPref().setStringDefault(id, this)
}

/**
 * gets string from strings.xml
 */
fun Int.get(context: Context): String {

    return context.getString(this)
}

/**
 * saves long in shared preference as default
 */
fun Long.addAsDefaultWithId(id: String) {

    QuickInjectable.quickPref().setLongDefault(id, this)
}

/**
 * removes duplicates from an array list
 */
fun ArrayList<String>.removeDuplicates(): ArrayList<String> {

    val end = this.size
    val set = ArrayList<String>()

    for (i in 0 until end) set.add(this[i])

    return set
}