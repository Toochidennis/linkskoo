package com.digitaldream.winskool.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.winskool.R
import com.digitaldream.winskool.models.StudentPaymentModel
import com.digitaldream.winskool.utils.UtilsFun
import java.util.*

class StudentPaymentCardAdapter(
    private var sContext: Context,
    private val sCardList: MutableList<StudentPaymentModel>,
    private val sOnCardClickListener: OnCardClickListener,
) : RecyclerView.Adapter<StudentPaymentCardAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.fragment_student_payment_scroll_view_item, parent, false
        )
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val paymentModel = sCardList[position]

        when (paymentModel.getTerm()) {
            "1" -> {
                holder.sessionTitle.text = String.format(
                    Locale.getDefault(), "%s " +
                            "%s", paymentModel.getSession(), "First Term Fees"
                )
            }

            "2" -> {
                holder.sessionTitle.text = String.format(
                    Locale.getDefault(), "%s " +
                            "%s", paymentModel.getSession(), "Second Term Fees"
                )
            }
            "3" -> {
                holder.sessionTitle.text = String.format(
                    Locale.getDefault(), "%s " +
                            "%s", paymentModel.getSession(), "Third Term Fees"
                )
            }
        }

        String.format(
            Locale.getDefault(), "%s%s",
            sContext.getString(R.string.naira),
            UtilsFun.currencyFormat(
                paymentModel.getAmount()!!.toDouble()
            )
        ).also { holder.termAmount.text = it }

    }

    override fun getItemCount() = sCardList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sessionTitle: TextView = itemView.findViewById(R.id.session_title)
        val termAmount: TextView = itemView.findViewById(R.id.term_amount)
        private val viewDetails: Button = itemView.findViewById(R.id.view_details_btn)
        private val payFee: Button = itemView.findViewById(R.id.btn_pay)

        init {
            payFee.setOnClickListener {
                sOnCardClickListener.makePayment(adapterPosition)
            }
            viewDetails.setOnClickListener {
                sOnCardClickListener.viewDetails(adapterPosition)
            }
        }
    }


    interface OnCardClickListener {
        fun viewDetails(position: Int)
        fun makePayment(position: Int)
    }

}