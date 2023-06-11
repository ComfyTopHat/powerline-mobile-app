package com.comfy.powerline.utils;

import androidx.recyclerview.widget.RecyclerView;

public class AppToolbox {

    public static void notifyNewMessageAdded(RecyclerView.Adapter adapter) {
        adapter.notifyItemInserted(adapter.getItemCount());
    }
}
