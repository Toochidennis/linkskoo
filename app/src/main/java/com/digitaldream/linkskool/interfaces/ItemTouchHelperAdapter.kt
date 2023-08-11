package com.digitaldream.linkskool.interfaces

import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.utils.DraggedItemDecoration

interface ItemTouchHelperAdapter {
    fun onItemMove(fromPosition: Int, toPosition: Int, )
    fun onItemDismiss(recyclerView: RecyclerView)
}