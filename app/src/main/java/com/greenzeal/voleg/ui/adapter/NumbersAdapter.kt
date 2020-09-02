package com.greenzeal.voleg.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.greenzeal.voleg.R
import com.greenzeal.voleg.models.PhoneNumber


class NumbersAdapter(private val listener: OnItemClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ITEM_VIEW_TYPE = 0

    private val list: ArrayList<PhoneNumber> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TextViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_number, parent, false))
    }

    override fun getItemViewType(position: Int): Int {
        return ITEM_VIEW_TYPE
    }

    fun addAll(list: ArrayList<PhoneNumber>) {
        this.list.addAll(list)
        val size: Int = this.list.size
        notifyItemRangeInserted(if (size == 0) 0 else size - 1, list.size)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        (holder as TextViewHolder).bind(item)
    }

    fun remove(position: Int) {
        if (position > -1) {
            list.removeAt(position)
                //  notifyItemRemoved(position)
          //  notifyItemRangeChanged(0,list.size);
            notifyDataSetChanged()
        }
    }

    fun add(item: PhoneNumber?) {
        list.add(item!!)
             notifyItemInserted(list.size - 1)
        //notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun getList() : MutableList<PhoneNumber>{
        return list
    }

    private fun getItem(position: Int): PhoneNumber? {
        return list[position]
    }

    fun clear() {
        val size: Int = list.size
        list.clear()
        notifyItemRangeRemoved(0, size)
    }

    fun updateProvider(number: String?, position: Int, selectedItems: MutableList<String>){
        list[position].number = number
        list[position].callProviders = selectedItems
        notifyItemChanged(position)
    }

    inner class TextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val providerButton = itemView.findViewById<ImageButton>(R.id.provider_button)
        private val deleteButton = itemView.findViewById<ImageButton>(R.id.delete_button)
        private val numberText = itemView.findViewById<EditText>(R.id.number_edit_text)

        fun bind(item: PhoneNumber) {
            numberText.setText(item.number)
            numberText.isFocusableInTouchMode = true
            numberText.requestFocus()
            val imm: InputMethodManager? =
                itemView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm?.showSoftInput(numberText, InputMethodManager.SHOW_IMPLICIT)

            val providers: Array<String> = arrayOf("Mobile", "Whatsapp", "Viber", "Telegram", "Land line")

            for(it in providers){
                if(item.callProviders!!.contains(it))
                    itemView.findViewById<ImageView>(getView(it)).visibility = View.VISIBLE
                else itemView.findViewById<ImageView>(getView(it)).visibility = View.GONE
            }

            numberText.addTextChangedListener{
                item.number = it.toString()
            }
            deleteButton.setOnClickListener {
                remove(adapterPosition)
            }
            providerButton.setOnClickListener{
                listener.showNumberDialog(adapterPosition, item.callProviders, item.number)
            }


        }

        private fun getView(provider: String): Int {
            return when (provider) {
                "Mobile" -> R.id.image_mobile
                "Whatsapp" -> R.id.image_whatsapp
                "Viber" -> R.id.image_viber
                "Telegram" -> R.id.image_telegram
                "Land line" -> R.id.image_landline
                else -> 0
            }
        }
    }

    interface OnItemClickListener {
        fun providerClick(provider: String?, numbers: MutableList<String>?)
        fun deleteClick(position: Int)
        fun showNumberDialog(
            adapterPosition: Int,
            callProviders: MutableList<String>?,
            number: String?
        )
    }
}


