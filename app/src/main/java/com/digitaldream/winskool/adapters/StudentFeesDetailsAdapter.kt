package com.digitaldream.winskool.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.winskool.R
import com.digitaldream.winskool.models.TermFeesDataModel
import java.util.*

class StudentFeesDetailsAdapter(
    private val sContext: Context,
    private val sFeeList: MutableList<TermFeesDataModel>
) : RecyclerView.Adapter<StudentFeesDetailsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.fragment_student_fee_details_item, parent, false
        )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val termFeesModel = sFeeList[position]
        holder.mFeeName.text = termFeesModel.getFeeName()
        if (termFeesModel.getFeeAmount().isNullOrBlank()) {
            holder.mFeeAmount.text = sContext.getString(R.string.zero_balance)
        } else {
            holder.mFeeAmount.text = termFeesModel.getFeeAmount()
        }
    }

    override fun getItemCount() = sFeeList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mFeeName: TextView = itemView.findViewById(R.id.fee_name)
        val mFeeAmount: TextView = itemView.findViewById(R.id.fee_amount)
    }
}