package com.netcheck.ncmapdemo.tool

import android.content.Context
import android.content.SharedPreferences
import com.netcheck.ncmapdemo.Constants

object SharedPrefs {
    private var sharedPreferences: SharedPreferences? = null

    /**
     * Initialize sharedPrefs here, only once
     *
     */
    @Synchronized
    fun instance(context: Context): SharedPreferences? {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(Constants.settings, Context.MODE_PRIVATE)
        }
        return sharedPreferences
    }
}