package com.digitaldream.winskool.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.winskool.R
import com.digitaldream.winskool.models.ExpenditureHistoryModel
import com.digitaldream.winskool.utils.FunctionUtils.capitaliseFirstLetter
import com.digitaldream.winskool.utils.FunctionUtils.currencyFormat
import java.util.*

class ExpenditureHistoryAdapter(
    private val sContext: Context,
    private val sExpenditureList: MutableList<ExpenditureHistoryModel>,
    private val sOnTransactionClickListener: OnTransactionClickListener,
) : RecyclerView.Adapter<ExpenditureHistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.fragment_history_expenditure_item, parent, false
        )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val adminModel = sExpenditureList[position]

        holder.mExpenditureName.text = capitaliseFirstLetter(adminModel.getVendorName()!!)
        holder.mExpenditureDate.text = adminModel.getDate()
        holder.mExpenditureType.text = adminModel.getType()

        String.format(
            Locale.getDefault(), "%s %s%s", "+", sContext.getString(R.string.naira),
            currencyFormat(adminModel.getAmount()!!.toDouble())
        ).also { holder.mExpenditureAmount.text = it }

    }

    override fun getItemCount() = sExpenditureList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mExpenditureName: TextView = itemView.findViewById(R.id.expenditure_name)
        val mExpenditureDate: TextView = itemView.findViewById(R.id.expenditure_date)
        val mExpenditureAmount: TextView = itemView.findViewById(R.id.expenditure_amount)
        val mExpenditureType: TextView = itemView.findViewById(R.id.expenditure_type)

        init {
            itemView.setOnClickListener {
                sOnTransactionClickListener.onTransactionClick(adapterPosition)
            }
        }

    }
}