package com.greenzeal.voleg.views

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.greenzeal.voleg.R

class NumbersDialogFragment : DialogFragment() {

    interface AddNumberListener {
        fun addNumberClick(number: String?, providers: MutableList<String>)
    }
    private var listener: AddNumberListener? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = try {
            context as AddNumberListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement AddNumberListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val items = arrayOf("Viber", "Whatsapp", "Telegram", "Land line", "Mobile")
        val selectedItems = mutableListOf("Viber", "Whatsapp", "Mobile")
        return AlertDialog.Builder(context!!)
            .setTitle(getString(R.string.add_phone))
            .setMultiChoiceItems(
                items,
                booleanArrayOf(true, true, false, false, true)
            ) { dialog, which, isChecked ->
                if(isChecked)
                    selectedItems.add(items[which])
                else if (selectedItems.contains(items[which])) {
                    selectedItems.remove(items[which])
                }
            }.setNegativeButton(getString(R.string.cancel)) { dialog, which -> dialog.dismiss() }
            .setPositiveButton(getString(R.string.done)) { dialog, which ->
                listener?.addNumberClick(null, selectedItems)
            }
            .create()
    }
}