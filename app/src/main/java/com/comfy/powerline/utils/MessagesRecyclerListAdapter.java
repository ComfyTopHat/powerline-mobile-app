package com.comfy.powerline.utils;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.comfy.powerline.MessageThread;
import com.comfy.powerline.R;


public class MessagesRecyclerListAdapter extends RecyclerView.Adapter<MessagesRecyclerListAdapter.ViewHolder>{
    private MessageDataList[] listdata;

    public MessagesRecyclerListAdapter(MessageDataList[] listdata) {
        this.listdata = listdata;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.message_list_item, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final MessageDataList myListData = listdata[position];
        // If the messages are for the convo preview:
        if (listdata[position].isMessagePreview()) {
            holder.leftMessageAuthor.setText(listdata[position].getAuthor());
            holder.leftMessagePreview.setText(listdata[position].getMessage());
            holder.rightMessageAuthor.setText(listdata[position].getDate());
            holder.leftImageView.setImageResource(listdata[position].getImgId());
        }
        // Otherwise check which messages are authorised by the logged in user and assign to the
        // correct side of the screen
        else {
            if (listdata[position].getSelfAuthored()) {
                holder.rightMessageAuthor.setText(listdata[position].getMessage());
                holder.rightMessagePreview.setText(listdata[position].getDate());
                holder.rightImageView.setImageResource(listdata[position].getImgId());
            } else {
                // holder.leftMessageAuthor.setText(listdata[position].getAuthor());
                //holder.left.setText(listdata[position].getDate());
                holder.leftMessagePreview.setText(listdata[position].getMessage());
                holder.leftImageView.setImageResource(listdata[position].getImgId());
            }
        }
        holder.relativeLayout.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), MessageThread.class);
            intent.putExtra("senderID", myListData.getSenderID());
            intent.putExtra("contact", myListData.getAuthor());
            view.getContext().startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return listdata.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView leftImageView;
        public ImageView rightImageView;
        public TextView leftMessageAuthor;
        public TextView rightMessageAuthor;
        public TextView rightMessagePreview;
        public TextView leftMessagePreview;
        public RelativeLayout relativeLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            this.rightMessageAuthor = (TextView) itemView.findViewById(R.id.right_message_author);
            this.rightImageView = (ImageView) itemView.findViewById(R.id.right_message_image);
            this.leftMessagePreview = (TextView) itemView.findViewById(R.id.left_message);
            this.rightMessagePreview = (TextView) itemView.findViewById(R.id.right_message);
            this.leftImageView = (ImageView) itemView.findViewById(R.id.left_message_image);
            this.leftMessageAuthor = (TextView) itemView.findViewById(R.id.left_author);
            relativeLayout = (RelativeLayout)itemView.findViewById(R.id.relativeLayout);
        }
    }
}