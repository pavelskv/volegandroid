package com.greenzeal.voleg.ui.adapter

import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.size
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.google.gson.Gson
import com.greenzeal.voleg.R
import com.greenzeal.voleg.models.Adv
import com.greenzeal.voleg.models.AdvData
import com.greenzeal.voleg.util.Constants
import com.greenzeal.voleg.util.Theme
import com.greenzeal.voleg.views.segmentedbutton.toDp
import java.util.*


class AdvAdapter(private val mGlide: RequestManager, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TEXT_ITEM_VIEW_TYPE = 0
    private val IMAGE_ITEM_VIEW_TYPE = 1
    private val LOADING_ITEM_VIEW_TYPE = 2

    private val list: ArrayList<Adv> = ArrayList()
    private var isLoadingAdded = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TEXT_ITEM_VIEW_TYPE -> TextViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_text, parent, false)
            )
            IMAGE_ITEM_VIEW_TYPE -> ImageViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
            )
            else -> TextViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_text, parent, false)
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == list.size - 1 && isLoadingAdded)
            return LOADING_ITEM_VIEW_TYPE
        return when (list[position].advType) {
            "picture" -> IMAGE_ITEM_VIEW_TYPE
            "image" -> IMAGE_ITEM_VIEW_TYPE
            "text" -> TEXT_ITEM_VIEW_TYPE
            else -> super.getItemViewType(position)
        }
    }

    fun addAll(list: ArrayList<Adv>) {
        this.list.addAll(list)
        val size: Int = this.list.size
        notifyItemRangeInserted(if (size == 0) 0 else size - 1, list.size)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        when (getItemViewType(position)) {
            TEXT_ITEM_VIEW_TYPE -> (holder as TextViewHolder).bind(item)
            IMAGE_ITEM_VIEW_TYPE -> {
                mGlide.load(Constants.BASE_URL + item.imageUrl)
                    .into((holder as ImageViewHolder).image)
                holder.bind(item)
            }
        }
    }

    private fun add(item: Adv?) {
        list.add(item!!)
        notifyItemInserted(list.size - 1)
    }

    override fun getItemId(position: Int): Long {
        return list[position].id.hashCode().toLong()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun getItem(position: Int): Adv? {
        return list[position]
    }

    fun clear() {
        val size: Int = list.size
        list.clear()
        notifyDataSetChanged()
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.item_image)!!
        private val providers: LinearLayout = itemView.findViewById(R.id.item_providers)
        private val acceptParcels = itemView.findViewById<LinearLayout>(R.id.accept_parcels)

        fun bind(item: Adv) {
            if (item.advData is String && (item.advData as String).isEmpty() || item.advData is Double)
                return

            val hashMap: HashMap<String, MutableList<String>> =
                HashMap()

            val json = Gson().toJson(item.advData)
            val model: AdvData = Gson().fromJson(json, AdvData::class.java)

            acceptParcels.visibility = if(model.acceptParcels == 1) View.VISIBLE else View.GONE

            for (phone in model.phoneNumbers!!) {
                for (provider in phone.callProviders!!) {
                    if (hashMap[provider] == null) {
                        hashMap[provider] = mutableListOf()
                    }

                    hashMap[provider]?.add(phone.number.toString().replace(Constants.PREFIX, ""))
                }
            }

            if(providers.childCount > 0)
                return

            for (i in hashMap.keys) {
                val imageButton = ImageButton(itemView.context)
                imageButton.scaleType = ImageView.ScaleType.CENTER_CROP
                imageButton.setImageResource(getImage(i))
                imageButton.background =
                    Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR)
                val layoutParams = LinearLayout.LayoutParams(48.toDp(), 48.toDp())
                layoutParams.setMargins(0, 8.toDp(), 8.toDp(), 8.toDp())
                imageButton.setOnClickListener { listener.providerClick(i, hashMap[i]) }
                itemView.setHasTransientState(true)
                providers.addView(imageButton, layoutParams)
            }

            itemView.setHasTransientState(false)
        }

        private fun getImage(provider: String): Int {
            return when (provider) {
                "mobile" -> R.drawable.icon_mobile
                "whatsapp" -> R.drawable.icon_whatsapp
                "viber" -> R.drawable.icon_viber
                "telegram" -> R.drawable.icon_telegram
                "landline" -> R.drawable.icon_landline
                else -> 0
            }
        }
    }


    inner class TextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title = itemView.findViewById<TextView>(R.id.item_text)
        private val providers = itemView.findViewById<LinearLayout>(R.id.item_providers)
        private val acceptParcels = itemView.findViewById<LinearLayout>(R.id.accept_parcels)

        fun bind(item: Adv) {
            if (item.advText!!.length > 150)
                title.textSize = 16f
            else title.textSize = 28f
            title.text = item.advText

            if (item.backgroundColor != null && item.backgroundColor!!.isNotEmpty()) {
                title.background = ColorDrawable(Color.parseColor("#" + item.backgroundColor))
            } else title.background = null

            if (item.advData is String && (item.advData as String).isEmpty() || item.advData is Double)
                return

            val hashMap: HashMap<String, MutableList<String>> =
                HashMap()

            val json = Gson().toJson(item.advData)
            val model: AdvData = Gson().fromJson(json, AdvData::class.java)

            acceptParcels.visibility = if(model.acceptParcels == 1) View.VISIBLE else View.GONE

            for (phone in model.phoneNumbers!!) {
                for (provider in phone.callProviders!!) {
                    if (hashMap[provider] == null) {
                        hashMap[provider] = mutableListOf()
                    }

                    hashMap[provider]?.add(phone.number.toString().replace(Constants.PREFIX, ""))
                }
            }

            if (providers.size > 0)
                return

            for (i in hashMap.keys) {
                val imageButton = ImageButton(itemView.context)
                imageButton.scaleType = ImageView.ScaleType.CENTER_CROP
                imageButton.setImageResource(getImage(i))
                imageButton.background =
                    Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR)
                val layoutParams = LinearLayout.LayoutParams(
                    48.toDp(),
                    48.toDp()
                )
                layoutParams.setMargins(
                    0,
                    8.toDp(),
                    8.toDp(),
                    8.toDp()
                )
                imageButton.setOnClickListener { listener.providerClick(i, hashMap[i]) }
                itemView.setHasTransientState(true)
                providers.addView(imageButton, layoutParams)
            }
            itemView.setHasTransientState(false)
        }

        private fun getImage(provider: String): Int {
            return when (provider) {
                "mobile" -> R.drawable.icon_mobile
                "whatsapp" -> R.drawable.icon_whatsapp
                "viber" -> R.drawable.icon_viber
                "telegram" -> R.drawable.icon_telegram
                "landline" -> R.drawable.icon_landline
                else -> 0
            }
        }
    }

    interface OnItemClickListener {
        fun providerClick(provider: String, numbers: MutableList<String>?)
    }
}


