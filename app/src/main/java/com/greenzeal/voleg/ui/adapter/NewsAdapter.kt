package com.greenzeal.voleg.ui.adapter

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.greenzeal.voleg.R
import com.greenzeal.voleg.models.News
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class NewsAdapter(private val mGlide: RequestManager, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TEXT_ITEM_VIEW_TYPE = 0
    private val LOADING_ITEM_VIEW_TYPE = 2

    private val list: ArrayList<News> = ArrayList()
    private var isLoadingAdded = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return NewsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        )
    }

    override fun getItemViewType(position: Int): Int {
        if (position == list.size - 1 && isLoadingAdded)
            return LOADING_ITEM_VIEW_TYPE
        return TEXT_ITEM_VIEW_TYPE
    }

    fun addAll(list: ArrayList<News>) {
        this.list.addAll(list)
        val size: Int = this.list.size
        notifyItemRangeInserted(if (size == 0) 0 else size - 1, list.size)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        when (getItemViewType(position)) {
            TEXT_ITEM_VIEW_TYPE -> (holder as NewsViewHolder).bind(item)
        }
    }

    private fun add(item: News?) {
        list.add(item!!)
        notifyItemInserted(list.size - 1)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun getItem(position: Int): News? {
        return list[position]
    }

    fun clear() {
        val size: Int = list.size
        list.clear()
        notifyItemRangeRemoved(0, size)
    }

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title = itemView.findViewById<TextView>(R.id.title)
        private val content = itemView.findViewById<TextView>(R.id.content)
        private val dateView = itemView.findViewById<TextView>(R.id.date)
        private val image = itemView.findViewById<ImageView>(R.id.image)

        fun bind(item: News) {
            title.text = item.title
            content.text = item.content

            //2020-08-31 10:12:21
            val dtStart = "2010-10-15T09:27:37Z"
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            try {
                val date: Date? = format.parse(dtStart)

                val formatDate = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
                if (date != null) {
                    dateView.text = formatDate.format(date)
                }
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            if (item.source != null)
                image.setImageResource(getImage(item.source))

            itemView.setOnClickListener { listener.onNewsClick(item.url) }
        }

        private val Int.dp: Int
            get() = (this * Resources.getSystem().displayMetrics.density).toInt()

        private fun getImage(source: String): Int {
            return when (source) {
                "ukrinform.ua" -> R.drawable.ukrinformua
                "pravda.com.ua" -> R.drawable.pravdacomua
                "tsn.ua" -> R.drawable.tsnua
                "dpsu.gov.ua" -> R.drawable.dpsugovua
                else -> 0
            }
        }
    }

    interface OnItemClickListener {
        fun onNewsClick(url: String?)
    }
}


