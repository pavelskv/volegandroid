package com.greenzeal.voleg.views

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class LoadingDialogFragment(private val layout: Int) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(context)
            .setView(layout)
            .setCancelable(false)
            .create()
    }
}