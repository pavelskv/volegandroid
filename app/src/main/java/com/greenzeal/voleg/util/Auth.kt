package com.greenzeal.voleg.util

import android.content.Context
import android.content.SharedPreferences
import com.greenzeal.voleg.BaseApp
import java.util.*

private const val PREF_UNIQUE_ID = "PREF_UNIQUE_ID"

class Auth {

    companion object {
        fun userId(): String? {

            val sharedPrefs: SharedPreferences =
                BaseApp.instance.getSharedPreferences(PREF_UNIQUE_ID, Context.MODE_PRIVATE)
            var uniqueID: String? = sharedPrefs.getString(PREF_UNIQUE_ID, null)

            if (uniqueID == null) {
                uniqueID = UUID.randomUUID().toString()
                val editor: SharedPreferences.Editor = sharedPrefs.edit()
                editor.putString(PREF_UNIQUE_ID, uniqueID)
                editor.apply()
            }

            return uniqueID
        }
    }
}
