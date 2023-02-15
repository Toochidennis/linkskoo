package com.digitaldream.winskool.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.digitaldream.winskool.DatabaseHelper
import com.digitaldream.winskool.R
import com.digitaldream.winskool.activities.Login
import com.digitaldream.winskool.adapters.StudentFeesDetailsAdapter
import com.digitaldream.winskool.models.LevelTable
import com.digitaldream.winskool.models.TermFeesDataModel
import com.digitaldream.winskool.utils.UtilsFun
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class SchoolFeesDetailsFragment : Fragment() {

    private lateinit var mCardView: CardView
    private lateinit var mSchoolName: TextView
    private lateinit var mTermTitle: TextView
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mPaymentLayout: RelativeLayout
    private lateinit var mFeeTotal: TextView
    private lateinit var mFeeTotal2: TextView
    private lateinit var mPayBtn: Button
    private lateinit var mErrorMessage: TextView
    private lateinit var mRefreshBtn: Button

    private var mYear: String? = null
    private var mTerm: String? = null
    private var mNameSchool: String? = null
    private var mLevelId: String? = null
    private var mDb: String? = null
    private var mTotal = 0.0
    private var mFeesList: MutableList<TermFeesDataModel> = arrayListOf()
    private lateinit var mAdapter: StudentFeesDetailsAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(
            R.layout.fragment_student_fees_details, container,
            false
        )

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        mRefreshBtn = view.findViewById(R.id.refresh_btn)
        mCardView = view.findViewById(R.id.card_view)
        mSchoolName = view.findViewById(R.id.school_name)
        mTermTitle = view.findViewById(R.id.fee_title)
        mRecyclerView = view.findViewById(R.id.details_recycler)
        mPaymentLayout = view.findViewById(R.id.payment_layout)
        mFeeTotal = view.findViewById(R.id.fee_total)
        mFeeTotal2 = view.findViewById(R.id.total2)
        mPayBtn = view.findViewById(R.id.pay_btn)
        mErrorMessage = view.findViewById(R.id.error_message)

        toolbar.apply {
            setNavigationIcon(R.drawable.arrow_left)
            title = "Fee Details"
            setNavigationOnClickListener {
                requireActivity().onBackPressedDispatcher
                    .onBackPressed()
            }
        }

        val sharedPreferences =
            requireContext().getSharedPreferences("loginDetail", Context.MODE_PRIVATE)
        mYear = sharedPreferences.getString("school_year", "")
        mNameSchool = sharedPreferences.getString("school_name", "")
        mTerm = sharedPreferences.getString("term", "")
        mLevelId = sharedPreferences.getString("level", "")
        mDb = sharedPreferences.getString("db", "")

        try {
            val previousYear = mYear!!.toInt() - 1
            val termText = when (mTerm) {
                "1" -> "First Term School Fee Charges for"
                "2" -> "Second Term School Fee Charges for"
                else -> "Third Term School Fee Charges for"
            }

            mSchoolName.text = mNameSchool

            mTermTitle.text = String.format(
                Locale.getDefault(),
                "%s %d/%s %s", termText, previousYear, mYear, "Session"
            )

        } catch (e: Exception) {
            e.printStackTrace()
        }

        mAdapter = StudentFeesDetailsAdapter(requireContext(), mFeesList)
        mRecyclerView.hasFixedSize()
        mRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        mRecyclerView.adapter = mAdapter

        getTermFees()

        mRefreshBtn.setOnClickListener {
            getTermFees()
        }

        return view
    }

    private fun getTermFees() {
        val progressFlower = ACProgressFlower.Builder(requireContext())
            .direction(ACProgressConstant.DIRECT_CLOCKWISE)
            .textMarginTop(10)
            .fadeColor(ContextCompat.getColor((context as AppCompatActivity), R.color.color_5))
            .build()
        progressFlower.setCancelable(false)
        progressFlower.setCanceledOnTouchOutside(false)
        progressFlower.show()
        val url =
            Login.urlBase + "/manageTermFees.php?list=1&&level=$mLevelId&&term=$mTerm&&year=$mYear"
        val stringRequest: StringRequest = object : StringRequest(
            Method.GET, url,
            { response: String ->
                Log.i("response", response)
                progressFlower.dismiss()

                try {
                    val jsonObjects = JSONObject(response)
                    for (objects in jsonObjects.keys()) {
                        val jsonObject = jsonObjects.getJSONObject(objects)
                        val feeName = jsonObject.getString("fee_name")
                        val feeAmount = jsonObject.getString("amount")

                        val termFeesModel = TermFeesDataModel()
                        if (!feeAmount.isNullOrBlank()) {
                            termFeesModel.setFeeName(feeName)
                            termFeesModel.setFeeAmount(feeAmount)
                            mFeesList.add(termFeesModel)
                            mFeesList.sortBy { it.getFeeName() }
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

                mTotal = 0.0
                for (amount in mFeesList) {
                    mTotal += amount.getFeeAmount()!!.toDouble()
                }
                mFeeTotal.text = String.format(
                    Locale.getDefault(), "%s%s",
                    requireActivity().getString(R.string.naira), UtilsFun.currencyFormat(mTotal)
                )

                mFeeTotal2.text = String.format(
                    Locale.getDefault(), "%s%s",
                    requireActivity().getString(R.string.naira), UtilsFun.currencyFormat(mTotal)
                )

                if (mFeesList.isEmpty()) {
                    mErrorMessage.isVisible = true
                    mErrorMessage.text = getString(R.string.no_data)
                    mCardView.isGone = true
                    mPaymentLayout.isGone = true
                } else if (mTotal == 0.0) {
                    mErrorMessage.isVisible = true
                    "Fees not set yet. Check back later!".also { mErrorMessage.text = it }
                    mCardView.isGone = true
                    mPaymentLayout.isGone = true
                    mRefreshBtn.isVisible = false
                } else {
                    mCardView.isGone = false
                    mPaymentLayout.isGone = false
                    mErrorMessage.isVisible = false
                    mRefreshBtn.isVisible = false
                }
                mAdapter.notifyDataSetChanged()

            }, { error: VolleyError ->
                error.printStackTrace()
                mErrorMessage.isVisible = true
                mRefreshBtn.isVisible = true
                mCardView.isGone = true
                mPaymentLayout.isGone = true
                mErrorMessage.text = getString(R.string.can_not_retrieve)
                progressFlower.dismiss()
            }) {
            override fun getParams(): Map<String, String> {
                val stringMap: MutableMap<String, String> = HashMap()
                stringMap["_db"] = mDb!!
                return stringMap
            }
        }
        val requestQueue: RequestQueue = Volley.newRequestQueue(requireContext())
        requestQueue.add(stringRequest)
    }
}