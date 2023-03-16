package com.digitaldream.winskool.fragments

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.winskool.R
import com.digitaldream.winskool.activities.Login
import com.digitaldream.winskool.models.AccountSetupDataModel
import com.digitaldream.winskool.utils.FunctionUtils.requestToServer
import com.digitaldream.winskool.utils.VolleyCallback
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM3 = "param3"


class AddExpenditureFragment : Fragment() {

    private lateinit var mCustomerName: TextView
    private lateinit var mCustomerPhone: TextView
    private lateinit var mExpenditureAmount: EditText
    private lateinit var mDate: EditText
    private lateinit var mRefNo: EditText
    private lateinit var mReferenceSpinner: Spinner
    private lateinit var mPurpose: EditText
    private lateinit var mAddBtn: Button
    private lateinit var mRefreshBtn: ImageView

    private var customerName: String? = null
    private var customerPhone: String? = null
    private var accountReference: String? = null
    private var mSpinnerList = mutableListOf<AccountSetupDataModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            customerName = it.getString(ARG_PARAM1)
            customerPhone = it.getString(ARG_PARAM3)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(name: String, phone: String) =
            AddExpenditureFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, name)
                    putString(ARG_PARAM3, phone)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(
            R.layout.fragment_add_expenditure, container,
            false
        )

        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        mCustomerName = view.findViewById(R.id.customer_name)
        mCustomerPhone = view.findViewById(R.id.customer_phone)
        mRefNo = view.findViewById(R.id.reference_number_input)
        mExpenditureAmount = view.findViewById(R.id.expenditure_amount_input)
        mDate = view.findViewById(R.id.date_input)
        mReferenceSpinner = view.findViewById(R.id.spinner_account_reference)
        mPurpose = view.findViewById(R.id.purpose_input)
        mAddBtn = view.findViewById(R.id.add_expenditure_btn)
        mRefreshBtn = view.findViewById(R.id.refresh_btn)

        toolbar.apply {
            setNavigationIcon(R.drawable.arrow_left)
            title = "Record Expenditure"
            setNavigationOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        }

        mCustomerName.text = customerName
        mCustomerPhone.text = customerPhone

        setDate()

        val sharedPreferences =
            requireContext().getSharedPreferences("loginDetail", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("account_reference", "")
        if (json!!.isNotEmpty()) {
            setSpinnerItem(json, "")
        } else {
            getAccountName()
        }

        mAddBtn.setOnClickListener {
            validateInput()
        }

        refreshSpinner()

        onSelectSpinnerItem()

        return view
    }

    private fun refreshSpinner() {
        mRefreshBtn.setOnClickListener {
            mSpinnerList.clear()
            getAccountName()
        }
    }

    private fun validateInput() {
        val amount = mExpenditureAmount.text.toString().trim()
        val date = mDate.text.toString().trim()
        val id = mRefNo.text.toString().trim()
        // val accountRef = mAccountReference.text.toString().trim()
        val purpose = mPurpose.text.toString().trim()

        if (amount.isEmpty() || date.isEmpty() || id.isEmpty() || purpose.isEmpty()
        ) {
            Toast.makeText(requireContext(), "Provide all fields", Toast.LENGTH_SHORT).show()
        } else {
            println(
                "name: $customerName phone: $customerPhone amount: $amount " +
                        "date: $date ref: $accountReference purpose: $purpose"
            )
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

    }

    private fun getAccountName() {
        val url = Login.urlBase + "/manageAccount.php?list=1"
        val hashMap = hashMapOf<String, String>()

        requestToServer(
            Request.Method.GET, url, requireContext(), hashMap,
            object : VolleyCallback {
                override fun onResponse(response: String) = setSpinnerItem(response, "save")

                override fun onError(error: VolleyError) {
                    Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT)
                        .show()
                }
            })

    }

    private fun onSelectSpinnerItem() {
        mReferenceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                accountReference = mSpinnerList[position].getAccountName()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

    }

    private fun setSpinnerItem(response: String, from: String) {
        try {
            val spinnerList = arrayListOf<String>()
            var spinnerAdapter: ArrayAdapter<String>? = null
            val jsonArray = JSONArray()
            JSONArray(response).also {
                for (i in 0 until it.length()) {
                    val jsonObject = it.getJSONObject(i)
                    val accountName = jsonObject.getString("account_name")
                    val spinnerObject = JSONObject().apply {
                        put("account_name", accountName)
                    }

                    jsonArray.put(spinnerObject)

                    val accountModel = AccountSetupDataModel()
                    accountModel.setAccountName(accountName)
                    mSpinnerList.add(accountModel)
                    mSpinnerList.sortBy(AccountSetupDataModel::getAccountName)
                }
            }
            if (from == "save") {
                requireContext().getSharedPreferences(
                    "loginDetail",
                    Context.MODE_PRIVATE
                ).edit()
                    .putString("account_reference", jsonArray.toString())
                    .apply()
            }
            for (names in mSpinnerList) {
                spinnerList.add(names.getAccountName()!!)
                spinnerAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    spinnerList
                )
            }
            mReferenceSpinner.adapter = spinnerAdapter

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setDate() {
        val calendar = Calendar.getInstance()
        val day = calendar[Calendar.DAY_OF_MONTH]
        val month = calendar[Calendar.MONTH]
        val year = calendar[Calendar.YEAR]

        mDate.setOnClickListener {

            DatePickerDialog(
                requireContext(),
                { _: DatePicker?, sYear: Int, sMonth: Int, sDayOfMonth: Int ->
                    val mont = sMonth + 1
                    val currentDate = "$sYear-$mont-$sDayOfMonth"
                    mDate.setText(currentDate)
                }, year, month, day
            ).show()
        }
    }

}