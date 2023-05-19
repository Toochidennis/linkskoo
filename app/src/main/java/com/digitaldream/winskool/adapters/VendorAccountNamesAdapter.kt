package com.digitaldream.winskool.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.winskool.R
import com.digitaldream.winskool.models.AccountSetupDataModel
import com.digitaldream.winskool.models.VendorModel
import com.digitaldream.winskool.utils.FunctionUtils.capitaliseFirstLetter

class VendorAccountNamesAdapter(
    private val sAccountList: MutableList<AccountSetupDataModel>?,
    private val sVendorList: MutableList<VendorModel>?,
    private val sOnItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<VendorAccountNamesAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout
                .bottom_sheet_vendor_account_names_item, parent, false
        )

        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (sAccountList == null) {
            holder.itemName.text = capitaliseFirstLetter(sVendorList!![position].customerName)
        } else {
            holder.itemName.text =
                capitaliseFirstLetter(sAccountList[position].mAccountName.toString())
        }
    }


    override fun getItemCount() = sAccountList?.size ?: sVendorList!!.size


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.item_name)

        init {
            sOnItemClickListener.onItemClick(adapterPosition)
        }
    }
}