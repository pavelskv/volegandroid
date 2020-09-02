package com.greenzeal.voleg.views

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class ImagePickDialogFragment : DialogFragment() {

        interface PickListener {
            fun pickButtonClicked(which: Int)
        }

        private var listener: PickListener? = null
        
        override fun onAttach(context: Context) {
            super.onAttach(context)
            listener = try {
                context as PickListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$context must implement PickListener")
            }
        }

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return AlertDialog.Builder(context!!)
                .setTitle("Завантажити зображення")
                .setItems(
                    arrayOf(
                        "Сфотографувати",
                        "Бібліотека зображень"
                    )
                ) { dialog: DialogInterface?, which: Int ->
                    listener?.pickButtonClicked(which)
                }.setNegativeButton("Скасувати") { dialog, which -> dialog.dismiss() }
                .create()
        }

    }