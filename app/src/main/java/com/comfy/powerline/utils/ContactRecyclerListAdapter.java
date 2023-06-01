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


public class ContactRecyclerListAdapter extends RecyclerView.Adapter<ContactRecyclerListAdapter.ViewHolder>{
    private ContactDataList[] listdata;

    public ContactRecyclerListAdapter(ContactDataList[] listdata) {
        this.listdata = listdata;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.message_list_item, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ContactDataList myListData = listdata[position];
        holder.textView.setText(listdata[position].getDescription());
        holder.dateView.setText(listdata[position].getDate());
        holder.imageView.setImageResource(listdata[position].getImgId());
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MessageThread.class);
                intent.putExtra("senderID", myListData.getContactID());
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
        public RelativeLayout relativeLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            this.dateView = (TextView) itemView.findViewById(R.id.left_message);
            this.imageView = (ImageView) itemView.findViewById(R.id.left_message_image);
            this.textView = (TextView) itemView.findViewById(R.id.left_author);
            relativeLayout = (RelativeLayout)itemView.findViewById(R.id.relativeLayout);
        }
    }
}