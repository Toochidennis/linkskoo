package com.digitaldream.winskool.interfaces

import android.view.View
import com.digitaldream.winskool.adapters.VendorAccountNamesAdapter

interface OnNameClickListener {
    fun onNameClick(holder: VendorAccountNamesAdapter.ViewHolder)
}

