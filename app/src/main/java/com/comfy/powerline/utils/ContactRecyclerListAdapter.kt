package com.comfy.powerline.utils

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.comfy.powerline.MessageThread
import com.comfy.powerline.R

class ContactRecyclerListAdapter(private var listdata: List<ContactDataList>) :
    RecyclerView.Adapter<ContactRecyclerListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem = layoutInflater.inflate(R.layout.message_list_item, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val myListData = listdata[position]
        holder.textView.text = listdata[position].description
        holder.dateView.text = listdata[position].date
        holder.imageView.setImageResource(listdata[position].imgId)
        holder.relativeLayout.setOnClickListener { view ->
            val intent = Intent(view.context, MessageThread::class.java)
            intent.putExtra("senderID", myListData.contactID)
            intent.putExtra("contact", myListData.description)
            view.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return listdata.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView
        var textView: TextView
        var dateView: TextView
        var relativeLayout: RelativeLayout

        init {
            dateView = itemView.findViewById<View>(R.id.left_message) as TextView
            imageView = itemView.findViewById<View>(R.id.left_message_image) as ImageView
            textView = itemView.findViewById<View>(R.id.left_author) as TextView
            relativeLayout = itemView.findViewById<View>(R.id.relativeLayout) as RelativeLayout
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filter(listdata: List<ContactDataList>, charText: String) {
        val filteredList: MutableList<ContactDataList> = ArrayList()
        if (charText.length == 0) {
            this.listdata = listdata
        } else {
            filteredList.add(ContactDataList("Send message to: $charText", 0, "NEW"))
            for (listDatum in listdata) {
                if (listDatum.description?.contains(charText) == true) {
                    filteredList.add(listDatum)
                }
            }
            this.listdata = filteredList
        }
        notifyDataSetChanged()
    }
}