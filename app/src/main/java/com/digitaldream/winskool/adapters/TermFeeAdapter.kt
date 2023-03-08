package com.digitaldream.winskool.adapters

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.winskool.R
import com.digitaldream.winskool.activities.Login
import com.digitaldream.winskool.models.TermFeesDataModel
import com.digitaldream.winskool.utils.FunctionUtils
import com.digitaldream.winskool.utils.FunctionUtils.requestToServer
import com.digitaldream.winskool.utils.VolleyCallback
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class TermFeeAdapter(
    private val sContext: Context,
    private val sTermFeesList: MutableList<TermFeesDataModel>,
    private val sTotal: TextView,
    private val sSaveBtn: Button,
    private val sLevelId: String,
) : RecyclerView.Adapter<TermFeeAdapter.ViewHolder>() {

    private var isOnTextChanged = false
    private var mFeeAmountTotal: Double = 0.0
    private var mFeeNameList = mutableMapOf<Int, String>()
    private var mFeeAmountList = mutableMapOf<Int, String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.activity_termly_fee_setup_item, parent, false
        )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val termFeesModel = sTermFeesList[position]
        val id = termFeesModel.getFeeId()
        holder.mFeeAmount.setText(termFeesModel.getFeeAmount())
        holder.mFeeName.text = termFeesModel.getFeeName()

        mFeeAmountTotal = 0.0
        try {
            mFeeNameList[id] = termFeesModel.getFeeName().toString()
            mFeeAmountList[id] = termFeesModel.getFeeAmount().toString()

            mFeeAmountList.forEach { (_, value) ->
                val total = value.toDouble()
                mFeeAmountTotal += total
            }
            sTotal.text = String.format(
                Locale.getDefault(), "%s%s", sContext
                    .getString(R.string.naira), FunctionUtils.currencyFormat(mFeeAmountTotal)
            )

        } catch (e: NumberFormatException) {
            mFeeAmountTotal = 0.0

            mFeeNameList[id] = "0"
            mFeeAmountList[id] = "0"

            mFeeAmountList.forEach { (_, value) ->
                val total = value.toDouble()
                mFeeAmountTotal += total
            }
            sTotal.text = String.format(
                Locale.getDefault(), "%s%s", sContext
                    .getString(R.string.naira), FunctionUtils.currencyFormat(mFeeAmountTotal)
            )
            e.printStackTrace()
        }

        holder.mRequired.isVisible =
            !(termFeesModel.getMandatory() == "null" || termFeesModel.getMandatory() == "0")

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
                        mFeeNameList[id] = termFeesModel.getFeeName().toString()
                        mFeeAmountList[id] = s.toString()

                        mFeeAmountList.forEach { (_, value) ->
                            val total = value.toDouble()
                            mFeeAmountTotal += total
                        }
                        sTotal.text = String.format(
                            Locale.getDefault(),
                            "%s%s",
                            sContext
                                .getString(R.string.naira),
                            FunctionUtils.currencyFormat(mFeeAmountTotal)
                        )

                    } catch (e: NumberFormatException) {
                        mFeeAmountTotal = 0.0

                        mFeeNameList[id] = "0"
                        mFeeAmountList[id] = "0"

                        mFeeAmountList.forEach { (_, value) ->
                            val total = value.toDouble()
                            mFeeAmountTotal += total
                        }
                        sTotal.text = String.format(
                            Locale.getDefault(),
                            "%s%s",
                            sContext
                                .getString(R.string.naira),
                            FunctionUtils.currencyFormat(mFeeAmountTotal)
                        )

                        e.printStackTrace()
                    }

                }
            }
        })

        sSaveBtn.setOnClickListener {
            setTermFees()
        }
    }

    override fun getItemCount() = sTermFeesList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mFeeName: TextView = itemView.findViewById(R.id.fee_name)
        val mFeeAmount: EditText = itemView.findViewById(R.id.fee_amount)
        val mRequired: ImageView = itemView.findViewById(R.id.mandatory)
    }

    private fun getTermFeeDataFromEditText(): JSONArray {
        val jsonArray = JSONArray()
        mFeeNameList.forEach { (key, value) ->
            if (value != "0") {
                val jsonObject = JSONObject()
                jsonObject.put("fee_id", key)
                jsonObject.put("fee_name", value)
                jsonObject.put("fee_amount", mFeeAmountList[key])
                jsonArray.put(jsonObject)
            }
        }
        return jsonArray
    }

    private fun setTermFees() {

        val sharedPreferences = sContext.getSharedPreferences(
            "loginDetail",
            Context.MODE_PRIVATE
        )
        val year = sharedPreferences.getString("school_year", "")
        val term = sharedPreferences.getString("term", "")

        val url = Login.urlBase + "/manageTermFees.php"
        val hashMap = hashMapOf<String, String>()
        hashMap["fees"] = getTermFeeDataFromEditText().toString()
        hashMap["level"] = sLevelId
        hashMap["year"] = year!!
        hashMap["term"] = term!!

        requestToServer(Request.Method.POST, url, sContext, hashMap,
            object : VolleyCallback {
                override fun onResponse(response: String) {
                    try {
                        val jsonObject = JSONObject(response)
                        when (jsonObject.getString("status")) {
                            "success" -> {
                                Toast.makeText(
                                    sContext, "Saved successfully", Toast
                                        .LENGTH_SHORT
                                ).show()
                            }
                            else -> Toast.makeText(sContext, "Failed", Toast.LENGTH_SHORT).show()
                        }

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }

                override fun onError(error: VolleyError) {

                }
            }
        )
    }

}