package com.greenzeal.voleg.views

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.greenzeal.voleg.R


class ConfirmDialogFragment(private val title: String, private val isUpdate: Boolean) : DialogFragment() {

    interface ConfirmListener {
        fun onConfirm(isUpdate: Boolean)
    }

    private var listener: ConfirmListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = try {
            context as ConfirmListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement ConfirmListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(context!!)
            .setMessage(title)
            .setPositiveButton(getString(R.string.ok)) { _, _ -> listener?.onConfirm(isUpdate) }
            .create()
    }

}