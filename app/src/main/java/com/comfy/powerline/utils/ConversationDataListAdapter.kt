package com.comfy.powerline.utils;

import android.annotation.SuppressLint;
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

import java.util.ArrayList;
import java.util.List;


public class ConversationDataListAdapter extends RecyclerView.Adapter<ConversationDataListAdapter.ViewHolder>{
    private List<ConversationDataList> listData;

    public ConversationDataListAdapter(List<ConversationDataList> listdata) {
        this.listData = listdata;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.conversation_list_item, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ConversationDataList myListData = listData.get(position);
        holder.dateView.setText(listData.get(position).getDate());
        holder.contactName.setText(listData.get(position).getContactName());
        holder.messagePreview.setText(listData.get(position).getMessagePreview());
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MessageThread.class);

                intent.putExtra("senderID", myListData.getContactID());
                intent.putExtra("contact", myListData.getContactName());
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filter(List<ConversationDataList> listdata, String charText) {
        List<ConversationDataList> filteredList = new ArrayList<>();
        if (charText.length() == 0) {
            this.listData = listdata;
        }
        else {
           // filteredList.add(new ConversationDataList("Send message to: " + charText, 0, "NEW"));
            for (ConversationDataList listDatum : listdata) {
                if ((listDatum.getMessagePreview().contains(charText)) || listDatum.getContactName().contains(charText)) {
                    filteredList.add(listDatum);
                }
            }this.listData = filteredList;
        }
        this.notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView contactImage;
        public TextView contactName;
        public TextView messagePreview;
        public TextView dateView;
        public RelativeLayout relativeLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            this.dateView = itemView.findViewById(R.id.conversation_date);
            this.contactImage = itemView.findViewById(R.id.contact_image);
            this.contactName = itemView.findViewById(R.id.contact_name);
            this.messagePreview = itemView.findViewById(R.id.conversation_message_preview);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
        }
    }
}