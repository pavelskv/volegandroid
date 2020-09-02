package com.greenzeal.voleg.util

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

class Utils {
    companion object{
        fun hideKeyboard(view: View?): Boolean {
            if (view == null) {
                return false
            }
            try {
                val inputManager = view.context
                    .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                return inputManager.hideSoftInputFromWindow(view.windowToken, 0)
            } catch (e: Exception) {
            }
            return false
        }
    }

}