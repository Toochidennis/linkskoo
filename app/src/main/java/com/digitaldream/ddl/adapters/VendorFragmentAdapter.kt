package com.digitaldream.ddl.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.ddl.R
import com.digitaldream.ddl.models.VendorModel
import com.digitaldream.ddl.utils.FunctionUtils.capitaliseFirstLetter

class VendorFragmentAdapter(
    private var sVendorList: MutableList<VendorModel>,
    private val sOnItemClickListener: OnItemClickListener,
) : RecyclerView.Adapter<VendorFragmentAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.fragment_vendor_item, parent, false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val vendorModel = sVendorList[position]

        holder.vendorName.text = capitaliseFirstLetter(vendorModel.customerName)
    }

    override fun getItemCount() = sVendorList.size

    fun filterList(filteredList: MutableList<VendorModel>) {
        sVendorList = filteredList
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val vendorName: TextView = itemView.findViewById(R.id.vendor_name)

        init {
            itemView.setOnClickListener {
                sOnItemClickListener.onItemClick(adapterPosition)
            }
        }
    }
}