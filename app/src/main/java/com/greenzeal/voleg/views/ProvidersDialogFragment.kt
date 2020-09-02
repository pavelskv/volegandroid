package com.greenzeal.voleg.views

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.greenzeal.voleg.R

class ProvidersDialogFragment(private val number: String?, private val adapterPosition: Int, private val selectedItems: MutableList<String>?) : DialogFragment() {

    interface ProviderListener {
        fun updateProvider(
            number: String?,
            selectedItems: MutableList<String>,
            adapterPosition: Int
        )
    }

    private var listener: ProviderListener? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = try {
            context as ProviderListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement AddNumberListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val items = arrayOf("Viber", "Whatsapp", "Telegram", "Land line", "Mobile")
        val selectedBoolean: MutableList<Boolean> = mutableListOf()
        for (item in items){
            if (selectedItems!!.contains(item))
                selectedBoolean.add(true)
            else selectedBoolean.add(false)
        }

        return AlertDialog.Builder(context!!)
            .setTitle(getString(R.string.add_phone))
            .setMultiChoiceItems(
                items,
                selectedBoolean.toBooleanArray()
            ) { dialog, which, isChecked ->
                if (isChecked)
                    selectedItems?.add(items[which])
                else if (selectedItems!!.contains(items[which])) {
                    selectedItems.remove(items[which])
                }
            }.setNegativeButton(getString(R.string.cancel)) { dialog, which -> dialog.dismiss() }
            .setPositiveButton(getString(R.string.done)) { dialog, which ->
                listener?.updateProvider(number, selectedItems!!, adapterPosition)
            }
            .create()
    }
}