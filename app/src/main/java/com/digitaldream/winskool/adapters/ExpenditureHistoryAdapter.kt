package com.digitaldream.winskool.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.winskool.R
import com.digitaldream.winskool.models.ExpenditureHistoryModel
import com.digitaldream.winskool.utils.FunctionUtils
import com.digitaldream.winskool.utils.FunctionUtils.capitaliseFirstLetter
import com.digitaldream.winskool.utils.FunctionUtils.currencyFormat
import java.util.*

class ExpenditureHistoryAdapter(
    private val sContext: Context,
    private val sExpenditureList: MutableList<ExpenditureHistoryModel>,
    private val sOnItemClickListener: OnItemClickListener,
) : RecyclerView.Adapter<ExpenditureHistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.fragment_history_expenditure_item, parent, false
        )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val adminModel = sExpenditureList[position]
        holder.bindItem(adminModel)

    }

    override fun getItemCount() = sExpenditureList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mExpenditureName: TextView = itemView.findViewById(R.id.expenditure_name)
        private val mExpenditureDate: TextView = itemView.findViewById(R.id.expenditure_date)
        private val mExpenditureAmount: TextView = itemView.findViewById(R.id.expenditure_amount)
        private val mExpenditureType: TextView = itemView.findViewById(R.id.expenditure_type)

        init {
            itemView.setOnClickListener {
                sOnItemClickListener.onItemClick(adapterPosition)
            }
        }

        fun bindItem(expenditureHistoryModel: ExpenditureHistoryModel) {
            mExpenditureName.text =
                capitaliseFirstLetter(expenditureHistoryModel.vendorName.toString())
            mExpenditureDate.text =
                FunctionUtils.formatDate2(expenditureHistoryModel.date.toString())
            mExpenditureType.text = expenditureHistoryModel.type


            String.format(
                Locale.getDefault(), "%s %s%s", "-", sContext.getString(R.string.naira),
                currencyFormat(expenditureHistoryModel.amount!!.toDouble())
            ).also { mExpenditureAmount.text = it }
        }

    }
}