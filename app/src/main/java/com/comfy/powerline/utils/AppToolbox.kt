package com.comfy.powerline.utils

import androidx.recyclerview.widget.RecyclerView

object AppToolbox {
    @JvmStatic
    fun notifyNewMessageAdded(adapter: RecyclerView.Adapter<*>) {
        adapter.notifyItemInserted(adapter.itemCount)
    }
}