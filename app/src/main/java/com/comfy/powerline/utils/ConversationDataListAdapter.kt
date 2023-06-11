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

class ConversationDataListAdapter(private var listData: List<ConversationDataList>) :
    RecyclerView.Adapter<ConversationDataListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem = layoutInflater.inflate(R.layout.conversation_list_item, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val myListData = listData[position]
        holder.dateView.text = listData[position].date
        holder.contactName.text = listData[position].contactName
        holder.messagePreview.text = listData[position].messagePreview
        holder.relativeLayout.setOnClickListener { view ->
            val intent = Intent(view.context, MessageThread::class.java)
            intent.putExtra("senderID", myListData.contactID)
            intent.putExtra("contact", myListData.contactName)
            view.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filter(listdata: List<ConversationDataList>, charText: String) {
        val filteredList: MutableList<ConversationDataList> = ArrayList()
        if (charText.length == 0) {
            listData = listdata
        } else {
            // filteredList.add(new ConversationDataList("Send message to: " + charText, 0, "NEW"));
            for (listDatum in listdata) {
                if (listDatum.messagePreview.contains(charText) || listDatum.contactName.contains(
                        charText
                    )
                ) {
                    filteredList.add(listDatum)
                }
            }
            listData = filteredList
        }
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var contactImage: ImageView
        var contactName: TextView
        var messagePreview: TextView
        var dateView: TextView
        var relativeLayout: RelativeLayout

        init {
            dateView = itemView.findViewById(R.id.conversation_date)
            contactImage = itemView.findViewById(R.id.contact_image)
            contactName = itemView.findViewById(R.id.contact_name)
            messagePreview = itemView.findViewById(R.id.conversation_message_preview)
            relativeLayout = itemView.findViewById(R.id.relativeLayout)
        }
    }
}