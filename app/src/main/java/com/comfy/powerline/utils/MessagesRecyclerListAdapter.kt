package com.comfy.powerline.utils

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

class MessagesRecyclerListAdapter(private val listdata: List<MessageDataList>) :
    RecyclerView.Adapter<MessagesRecyclerListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem = layoutInflater.inflate(R.layout.message_list_item, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val myListData = listdata[position]
        // If the messages are for the convo preview:
        if (listdata[position].isMessagePreview) {
            holder.leftMessageAuthor.text = listdata[position].author
            holder.leftMessagePreview.text = listdata[position].message
            holder.rightMessageAuthor.text = listdata[position].date
            holder.leftImageView.setImageResource(listdata[position].imgId)
        } else {
            if (listdata[position].selfAuthored == true) {
                holder.rightMessageAuthor.text = listdata[position].message
                holder.rightMessagePreview.text = listdata[position].date
                holder.rightImageView.setImageResource(listdata[position].imgId)
            } else {
                // holder.leftMessageAuthor.setText(listdata[position].getAuthor());
                //\holder.left.setText(listdata[position].getDate());
                holder.leftMessagePreview.text = listdata[position].message
                holder.leftImageView.setImageResource(listdata[position].imgId)
            }
        }
        holder.relativeLayout.setOnClickListener { view: View ->
            val intent = Intent(view.context, MessageThread::class.java)
            intent.putExtra("senderID", myListData.senderID)
            intent.putExtra("contact", myListData.author)
            view.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return listdata.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var leftImageView: ImageView
        var rightImageView: ImageView
        var leftMessageAuthor: TextView
        var rightMessageAuthor: TextView
        var rightMessagePreview: TextView
        var leftMessagePreview: TextView
        var relativeLayout: RelativeLayout

        init {
            leftMessageAuthor = itemView.findViewById<View>(R.id.left_author) as TextView
            rightMessageAuthor = itemView.findViewById<View>(R.id.right_message_author) as TextView
            leftImageView = itemView.findViewById<View>(R.id.left_message_image) as ImageView
            rightImageView = itemView.findViewById<View>(R.id.right_message_image) as ImageView
            leftMessagePreview = itemView.findViewById<View>(R.id.left_message) as TextView
            rightMessagePreview = itemView.findViewById<View>(R.id.right_message) as TextView
            relativeLayout = itemView.findViewById<View>(R.id.relativeLayout) as RelativeLayout
        }
    }
}