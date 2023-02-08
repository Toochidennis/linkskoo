package com.digitaldream.winskool.adapters

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.winskool.R
import com.digitaldream.winskool.models.FeeTypeModel
import java.text.DecimalFormat
import java.util.*

class TermFeeAdapter(
    private val sContext: Context,
    private val sFeeNameList: MutableList<FeeTypeModel>,
    private val sTotal: TextView,
) : RecyclerView.Adapter<TermFeeAdapter.ViewHolder>() {

    private var isOnTextChanged = false
    private var mFeeAmountTotal: Double = 0.0
    private var mFeeNameList = mutableMapOf<Int, String>()
    private var mFeeAmountList = mutableMapOf<Int, String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout
                .fragment_termly_fee_setup_item, parent, false
        )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val feeTypeModel = sFeeNameList[position]
        val id = feeTypeModel.getFeeId()

        holder.mFeeName.text = feeTypeModel.getFeeName()

        holder.mFeeAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                isOnTextChanged = true
            }

            override fun afterTextChanged(s: Editable?) {
                if (isOnTextChanged) {
                    isOnTextChanged = false

                    mFeeAmountTotal = 0.0
                    try {
                        mFeeNameList[id] = feeTypeModel.getFeeName().toString()
                        mFeeAmountList[id] = s.toString()

                        mFeeAmountList.forEach { (_, value) ->
                            val total = value.toDouble()
                            mFeeAmountTotal += total
                        }
                        sTotal.text = String.format(Locale.getDefault(), "%s%s",sContext
                            .getString(R.string.naira),currencyFormat(mFeeAmountTotal))

                        println("Name: $mFeeNameList")
                        println("Amount: $mFeeAmountList")
                        println("Total: $sTotal.text")

                    } catch (e: NumberFormatException) {
                        mFeeAmountTotal = 0.0

                        mFeeNameList[id] = "0"
                        mFeeAmountList[id] = "0"

                        mFeeAmountList.forEach { (_, value) ->
                            val total = value.toDouble()
                            mFeeAmountTotal += total
                        }
                        sTotal.text = String.format(Locale.getDefault(), "%s%s",sContext
                            .getString(R.string.naira),currencyFormat(mFeeAmountTotal))

                        println("Name: $mFeeNameList")
                        println("Amount: $mFeeAmountList")
                        println("Total: $sTotal.text")

                        e.printStackTrace()
                    }

                }
            }
        })
    }

    override fun getItemCount() = sFeeNameList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mFeeName: TextView = itemView.findViewById(R.id.fee_name)
        val mFeeAmount: EditText = itemView.findViewById(R.id.fee_amount)

    }

    private fun currencyFormat(number: Double): String{
        val formatter = DecimalFormat("###,###,##0.00")
        return formatter.format(number)
    }
}