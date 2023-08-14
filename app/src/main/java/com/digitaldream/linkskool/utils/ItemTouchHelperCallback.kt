package com.digitaldream.linkskool.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.interfaces.ItemTouchHelperAdapter

class ItemTouchHelperCallback(
    private val adapter: ItemTouchHelperAdapter,
) : ItemTouchHelper.Callback() {

    private var isDragging = false
    private var draggedItemDecoration: DraggedItemDecoration? = null
    override fun isLongPressDragEnabled(): Boolean = true

    override fun isItemViewSwipeEnabled(): Boolean = false

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        adapter.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // Handle swipe-to-dismiss if needed
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        return makeMovementFlags(dragFlags, 0)
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)

        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG && !isDragging) {
//            viewHolder?.let {
//                draggedItemDecoration = DraggedItemDecoration(it)
//                recyclerView.addItemDecoration(draggedItemDecoration!!)
//                draggedItemDecoration?.setDragging(true)
//                recyclerView.invalidateItemDecorations()
//            }

            viewHolder?.itemView?.alpha = .9f
            viewHolder?.itemView?.animate()?.scaleX(.9f)?.scaleY(.9f)?.setDuration(200)?.start()
            isDragging = true
        }
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        viewHolder.itemView.alpha = 1.0f
        viewHolder.itemView.scaleX = 1.0f
        viewHolder.itemView.scaleY = 1.0f
        isDragging = false

        adapter.onItemDismiss(recyclerView)
    }

}

class DraggedItemDecoration(private val draggedViewHolder: RecyclerView.ViewHolder) :
    RecyclerView.ItemDecoration() {

    private var isDragging = false

    fun setDragging(isDragging: Boolean) {
        this.isDragging = isDragging
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (isDragging && parent.getChildViewHolder(view) === draggedViewHolder) {
            val draggedY = view.y
            val topMargin = maxOf(0f, draggedY - view.height)
            outRect.top = topMargin.toInt()
        }
    }
}
