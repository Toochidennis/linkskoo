package com.digitaldream.linkskool.interfaces

interface ItemTouchHelperAdapter {
    fun onItemPressed()
    fun onItemMove(fromPosition: Int, toPosition: Int)
    fun onItemDismiss(position:Int)
}