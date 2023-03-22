package com.digitaldream.ddl.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.ddl.R
import com.digitaldream.ddl.models.AdminPaymentModel
import com.digitaldream.ddl.utils.FunctionUtils
import java.util.*

class ReceiptsHistoryAdapter(
    private val sContext: Context,
    private val sTransactionList: MutableList<AdminPaymentModel>,
    private val sOnItemClickListener: OnItemClickListener,
) : RecyclerView.Adapter<ReceiptsHistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.fragment_history_receipts_item, parent, false
        )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val adminModel = sTransactionList[position]

        holder.mStudentName.text = FunctionUtils.capitaliseFirstLetter(adminModel.getStudentName()!!)
        holder.mReceiptDate.text = adminModel.getTransactionDate()
        holder.mStudentClass.text = adminModel.getClassName()

        String.format(
            Locale.getDefault(), "%s %s%s", "+", sContext.getString(R.string.naira),
            FunctionUtils.currencyFormat(adminModel.getReceivedAmount()!!.toDouble())
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

}