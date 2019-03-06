package com.rzahr.quicktools

import android.content.Context
import android.preference.PreferenceManager
import javax.inject.Inject

@Suppress("unused")
class QuickPref @Inject constructor(val context: Context) {

    /**
     * Set shared boolean value.
     * @param id    the id
     * @param value the value
     */
    fun setBoolean(id: String, value: Boolean?) {

        val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        editor.putBoolean(id, value!!)
        editor.apply()
    }

    /**
     * Set shared long value.
     * @param id    the id
     * @param value the value
     */
    fun setLong(id: String, value: Long?) {

        val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        editor.putLong(id, value!!)
        editor.apply()
    }

    /**
     * Get shared long value long.
     * @param id the id
     * @return the long
     */
    fun getLong(id: String): Long {

      return  PreferenceManager.getDefaultSharedPreferences(context).getLong(id, 0)
    }

    /**
     * Set shared string value.
     * @param id    the id
     * @param value the value
     */
    fun setString(id: String, value: String) {

        val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        editor.putString(id, value)
        editor.apply()
    }

    /**
     * Check shared pref value if exist boolean.
     * @param value the value
     * @return the boolean
     */
    fun checkSharedPrefValueIfExist(value: String): Boolean {

        if (PreferenceManager.getDefaultSharedPreferences(context).contains(value)) {
            return true
        }

        return false
    }


    /**
     * Set shared string default value.
     * @param id    the id
     * @param value the value
     */
    fun setStringDefault(id: String, value: String) {

        if (!checkSharedPrefValueIfExist(id)) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putString(id, value)
            editor.apply()

        }
    }


    /**
     * Set shared long default value.
     * @param id    the id
     * @param value the value
     */
    fun setLongDefault(id: String, value: Long?) {

        if (!checkSharedPrefValueIfExist(id)) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(id, value!!)
            editor.apply()
        }
    }

    fun setIntValue(id: String, value: Int) {

        val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        editor.putInt(id, value)
        editor.apply()
    }

    /**
     * Get shared string value string.
     * @param id the id
     * @return the string
     */
    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    fun get(id: String): String {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(id, "")
    }

    /**
     * Get shared string value string.
     * @param id the id
     * @return the string
     */
    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    fun getInt(id: String): Int {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(id, -1)
    }

    /**
     * Get shared boolean value boolean.
     * @param id the id
     * @return the boolean
     */
    fun getBoolean(id: String): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(id, false)
    }
}