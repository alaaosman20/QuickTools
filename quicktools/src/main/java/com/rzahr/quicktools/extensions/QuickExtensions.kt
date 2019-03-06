@file:Suppress("unused")

package com.rzahr.quicktools.extensions

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import com.rzahr.quicktools.QuickInjectable
import java.util.*

/**
 * @author Rashad Zahr
 */

/**
 * saves instance state of a string
 * @param id: the id pointing to this string value
 * @param outState: the bundle where the value is saved in
 */
fun String.saveStateOf(id: String, outState: Bundle?) {

    outState?.putString(id, this)
}

/**
 * saves instance state of a boolean
 * @param id: the id pointing to this boolean value
 * @param outState: the bundle where the value is saved in
 */
fun Boolean.saveStateOf(id: String, outState: Bundle?) {

    outState?.putBoolean(id, this)
}

/**
 * saves instance state of a parcelable array list
 * @param id: the id pointing to this array list value
 * @param outState: the bundle where the value is saved in
 */
fun ArrayList<out Parcelable>.saveStateOf(id: String, outState: Bundle?) {

    outState?.putParcelableArrayList(id, this)
}

/**
 * saves instance state of a parcelable
 * @param id: the id pointing to this parcelable value
 * @param outState: the bundle where the value is saved in
 */
fun Parcelable.saveStateOf(id: String, outState: Bundle?) {

    outState?.putParcelable(id, this)
}

/**
 * gets the saved instance state of a parcelable array list
 * @param id: the id pointing to this object value
 */
fun <T : Parcelable> Bundle?.getStateOf(id: String): ArrayList<T>? {

    return this?.getParcelableArrayList(id)
}

/**
 * gets the saved instance state of a parcelable
 * the parcelable attribute is needed to differentiate between this method and the one for the array list of parcelable
 * @param id: the id pointing to this object value
 * @param parcelable: unused parameter
 */
fun <T : Parcelable> Bundle?.getStateOf(id: String, @Suppress("UNUSED_PARAMETER") parcelable: Int): T? {

    return this?.getParcelable(id)
}

/**
 * gets the saved instance state of a string
 * @param id: the id pointing to this string value
 * @param default: the default value of the string if it does not exist
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
 * @param id: the id pointing to this boolean value
 * @param default: the default value of the boolean if it does not exist
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
 * @param id: the id pointing to this integer value
 */
fun Int.addWithId(id: String) {

    QuickInjectable.quickPref().setIntValue(id, this)
}

/**
 * saves string in shared preference
 * @param id: the id pointing to this string value
 */
fun String.addWithId(id: String) {

    QuickInjectable.quickPref().setString(id, this)
}

/**
 * saves double in shared preference
 * @param id: the id pointing to this double value
 */
fun Double.addWithId(id: String) {

    QuickInjectable.quickPref().setString(id, this.toString())
}

/**
 * saves long in shared preference
 * @param id: the id pointing to this long value
 */
fun Long.addWithId(id: String) {

    QuickInjectable.quickPref().setLong(id, this)
}

/**
 * saves boolean in shared preference
 * @param id: the id pointing to this boolean value
 */
fun Boolean.addWithId(id: String) {

    QuickInjectable.quickPref().setBoolean(id, this)
}

/**
 * saves string in shared preference as default
 * @param id: the id pointing to this string value
 */
fun String.addAsDefaultWithId(id: String) {

    QuickInjectable.quickPref().setStringDefault(id, this)
}

/**
 * gets string from strings.xml
 * @param context: the context used to get the string value. in case the value is not passed then the application context is provided
 */
fun Int.get(context: Context = QuickInjectable.applicationContext()): String {

    return context.getString(this)
}

/**
 * saves long in shared preference as default
 * @param id: the id pointing to this string value
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