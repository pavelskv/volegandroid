package com.greenzeal.voleg.views

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.greenzeal.voleg.R


class AcceptParcelsDialog : DialogFragment() {

    interface AcceptListener {
        fun onAccept(accept: Int)
    }

    private var listener: AcceptListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = try {
            context as AcceptListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement AcceptListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(context!!)
            .setMessage(getString(R.string.are_you_taking_parcels))
            .setPositiveButton(getString(R.string.yes)) { _, _ -> listener?.onAccept(1) }
            .setNegativeButton(getString(R.string.no)) { _, _ -> listener?.onAccept(2)}
            .setNeutralButton(getString(R.string.cancel)){ _, _ -> dismiss() }
            .create()
    }

}