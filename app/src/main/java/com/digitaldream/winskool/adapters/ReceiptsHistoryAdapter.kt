package com.digitaldream.winskool.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.winskool.R
import com.digitaldream.winskool.models.AdminPaymentModel
import com.digitaldream.winskool.utils.FunctionUtils.capitaliseFirstLetter
import com.digitaldream.winskool.utils.FunctionUtils.currencyFormat
import com.digitaldream.winskool.utils.FunctionUtils.formatDate2
import java.util.*

class ReceiptsHistoryAdapter(
    private val sContext: Context,
    private val sTransactionList: MutableList<AdminPaymentModel>,
    private val sOnItemClickListener: OnItemClickListener,
) : RecyclerView.Adapter<ReceiptsHistoryAdapter.ViewHolder>(), Filterable {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.fragment_history_receipts_item, parent, false
        )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val adminModel = sTransactionList[position]

        holder.mStudentName.text = capitaliseFirstLetter(adminModel.mStudentName!!)
        holder.mReceiptDate.text = formatDate2(adminModel.mTransactionDate!!)
        holder.mStudentClass.text = adminModel.mClassName

        String.format(
            Locale.getDefault(), "%s %s%s", "+", sContext.getString(R.string.naira),
            currencyFormat(adminModel.mReceivedAmount!!.toDouble())
        ).also { holder.mReceiptAmount.text = it }

    }

    override fun getItemCount() = sTransactionList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mStudentName: TextView = itemView.findViewById(R.id.student_name)
        val mReceiptDate: TextView = itemView.findViewById(R.id.receipt_date)
        val mReceiptAmount: TextView = itemView.findViewById(R.id.receipt_amount)
        val mStudentClass: TextView = itemView.findViewById(R.id.student_class)

        init {
            itemView.setOnClickListener {
                sOnItemClickListener.onItemClick(adapterPosition)
            }
        }

    }


    @Suppress("UNCHECKED_CAST")
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val transactionList = sTransactionList
                val filteredList = mutableListOf<AdminPaymentModel>()

                if (constraint.isNullOrEmpty())
                    filteredList.addAll(transactionList)
                else
                    transactionList.forEach {

                        if (it.mStudentName!!.lowercase()
                                .contains(
                                    constraint.toString().lowercase().trim(),
                                )
                        ) {
                            filteredList.add(it)
                        }
                    }

                return FilterResults().apply { values = filteredList }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                sTransactionList.clear()
                sTransactionList.addAll(results?.values as MutableList<AdminPaymentModel>)
                notifyDataSetChanged()
            }
        }
    }


}