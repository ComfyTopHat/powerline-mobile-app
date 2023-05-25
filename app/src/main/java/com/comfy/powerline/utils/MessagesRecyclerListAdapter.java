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
        View listItem= layoutInflater.inflate(R.layout.list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final MessageDataList myListData = listdata[position];
        holder.textView.setText(listdata[position].getDescription());
        holder.dateView.setText(listdata[position].getDate());
        holder.messageView.setText(listdata[position].getMessage());
        holder.imageView.setImageResource(listdata[position].getImgId());


        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MessageThread.class);
                String senderID = myListData.getSenderID();
                intent.putExtra("senderID", myListData.getSenderID());
                intent.putExtra("contact", myListData.getDescription());
                view.getContext().startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return listdata.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;
        public TextView dateView;
        public TextView messageView;
        public RelativeLayout relativeLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            this.messageView = (TextView) itemView.findViewById(R.id.message_preview_view);
            this.dateView = (TextView) itemView.findViewById(R.id.dateView);
            this.imageView = (ImageView) itemView.findViewById(R.id.imageView);
            this.textView = (TextView) itemView.findViewById(R.id.textView);
            relativeLayout = (RelativeLayout)itemView.findViewById(R.id.relativeLayout);
        }
    }
}