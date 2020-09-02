package com.greenzeal.voleg.views

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import java.util.*

class CallDialogFragment(
    private val provider: String,
    private val numbers: MutableList<String>?
) : DialogFragment() {

    interface CallListener {
        fun onCall(provider: String, number: String)
    }

    private var listener: CallListener? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = try {
            context as CallListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement CallListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(context!!)
            .setTitle(provider.toUpperCase(Locale.ROOT))
            .setItems(numbers!!.toTypedArray()) { _: DialogInterface?, which: Int ->
                listener!!.onCall(provider, numbers[which])
            }.setNegativeButton("Скасувати") { dialog, _ -> dialog.dismiss() }
            .create()
    }

}