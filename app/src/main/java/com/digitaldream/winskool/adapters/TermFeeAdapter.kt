package com.digitaldream.winskool.adapters

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.digitaldream.winskool.R
import com.digitaldream.winskool.activities.Login
import com.digitaldream.winskool.models.TermFeesDataModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.DecimalFormat
import java.util.*

class TermFeeAdapter(
    private val sContext: Context,
    private val sTermFeesList: MutableList<TermFeesDataModel>,
    private val sTotal: TextView,
    private val sSaveBtn: Button,
    private val sLevelId: String
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
                    .getString(R.string.naira), currencyFormat(mFeeAmountTotal)
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
                    .getString(R.string.naira), currencyFormat(mFeeAmountTotal)
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
                            Locale.getDefault(), "%s%s", sContext
                                .getString(R.string.naira), currencyFormat(mFeeAmountTotal)
                        )

                        println("Total: $sTotal.text")
                        println("Total: $mFeeNameList.text")
                        println("Total: $mFeeAmountList.text")

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
                                .getString(R.string.naira), currencyFormat(mFeeAmountTotal)
                        )

                        println("Total: $sTotal.text")
                        println("Total: $mFeeNameList.text")
                        println("Total: $mFeeAmountList.text")

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

    private fun currencyFormat(number: Double): String {
        val formatter = DecimalFormat("###,###,##0.00")
        return formatter.format(number)
    }

    private fun getData(): JSONArray {
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
        println("Package: ${getData()}")

        val sharedPreferences = sContext.getSharedPreferences(
            "loginDetail",
            Context.MODE_PRIVATE
        )
        val mDb = sharedPreferences.getString("db", "")
        val year = sharedPreferences.getString("school_year", "")
        val term = sharedPreferences.getString("term", "")

        println("year: $year  term: $term")

        val url = Login.urlBase + "/manageTermFees.php"
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST,
            url,
            { response: String ->
                Log.i("response", response)
                try {
                    val jsonObject = JSONObject(response)
                    when (jsonObject.getString("status")) {
                        "success" -> {
                            Toast.makeText(
                                sContext, "Saved successfully", Toast
                                    .LENGTH_SHORT
                            ).show()
                            /* mFeeAmountList.clear()
                             mFeeNameList.clear()*/
                        }
                        else -> Toast.makeText(sContext, "Failed", Toast.LENGTH_SHORT).show()
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }, { error: VolleyError ->
                error.printStackTrace()

            }) {
            override fun getParams(): Map<String, String> {
                val stringMap: MutableMap<String, String> = HashMap()
                stringMap["fees"] = getData().toString()
                stringMap["level"] = sLevelId
                stringMap["year"] = year!!
                stringMap["term"] = term!!
                stringMap["_db"] = mDb!!
                return stringMap
            }
        }
        val requestQueue: RequestQueue = Volley.newRequestQueue(sContext)
        requestQueue.add(stringRequest)

    }

}