package com.digitaldream.winskool.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.digitaldream.winskool.R
import com.digitaldream.winskool.activities.Login
import com.digitaldream.winskool.adapters.AccountSetupAdapter
import com.digitaldream.winskool.dialog.AccountSetupDialog
import com.digitaldream.winskool.models.AccountSetupDataModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AccountSetupFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private var mDb: String? = null

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: AccountSetupAdapter
    private lateinit var mAccountList: MutableList<AccountSetupDataModel>
    private lateinit var mErrorMessage: TextView
    private lateinit var mAddAccountBtn: FloatingActionButton
    private lateinit var mRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AccountSetupFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_account_setup, container, false)

        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        mAddAccountBtn = view.findViewById(R.id.add_account_btn)
        mRecyclerView = view.findViewById(R.id.account_recycler)
        mErrorMessage = view.findViewById(R.id.error_message)
        mRefreshLayout = view.findViewById(R.id.swipeRefresh)

        ((activity as AppCompatActivity)).setSupportActionBar(toolbar)
        val actionBar = ((activity as AppCompatActivity)).supportActionBar

        actionBar!!.apply {
            setHomeButtonEnabled(true)
            title = "Account Settings"
            setHomeAsUpIndicator(R.drawable.arrow_left)
            setDisplayHomeAsUpEnabled(true)
        }

        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        val sharedPreferences = requireActivity().getSharedPreferences(
            "loginDetail",
            Context.MODE_PRIVATE
        )
        mDb = sharedPreferences.getString("db", "")

        mAccountList = arrayListOf()
        mAdapter = AccountSetupAdapter(requireContext(), mAccountList, mErrorMessage)
        mRecyclerView.hasFixedSize()
        mRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        mRecyclerView.adapter = mAdapter

        getAccount() //get Accounts
        openDialog()
        refreshData() // refresh accounts

        return view
    }

    private fun refreshData() {
        mRefreshLayout.setColorSchemeColors(
            ContextCompat.getColor(
                requireContext(),
                R.color.color_5
            )
        )
        mRefreshLayout.setOnRefreshListener {
            mAccountList.clear()
            getAccount()
            mRefreshLayout.isRefreshing = false
        }
    }

    private fun openDialog() {
        mAddAccountBtn.setOnClickListener {
            val accountDialog = AccountSetupDialog(
                requireContext(),
                "add", "",
                "",
                "",
                ""
            )

            accountDialog.apply {
                setCancelable(true)
                show()
            }
            val window = accountDialog.window
            window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

    }
    private fun getAccount() {
        val progressFlower = ACProgressFlower.Builder(requireContext())
            .direction(ACProgressConstant.DIRECT_CLOCKWISE)
            .textMarginTop(10)
            .fadeColor(ContextCompat.getColor((context as AppCompatActivity), R.color.color_5))
            .build()
        progressFlower.setCancelable(false)
        progressFlower.setCanceledOnTouchOutside(false)
        progressFlower.show()
        val url = Login.urlBase + "/manageAccount.php?list=1"
        val stringRequest: StringRequest = object : StringRequest(
            Method.GET,
            url,
            { response: String ->
                Log.i("response", response)
                progressFlower.dismiss()
                try {

                    val jsonArray = JSONArray(response)
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val id = jsonObject.getString("id")
                        val accountName = jsonObject.getString("account_name")
                        val accountId = jsonObject.getString("account_id")
                        val accountType = jsonObject.getString("account_type")

                        val accountModel = AccountSetupDataModel()
                        accountModel.setId(id.toInt())
                        accountModel.setAccountId(accountId)
                        accountModel.setAccountName(accountName)
                        accountModel.setAccountType(accountType)

                        mAccountList.add(accountModel)
                        mAccountList.sortBy { it.getAccountName() }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

                if (mAccountList.isEmpty()) {
                    mErrorMessage.isVisible = true
                    mErrorMessage.text = getString(R.string.no_data)
                    mRecyclerView.isGone = true
                } else {
                    mRecyclerView.isGone = false
                    mErrorMessage.isVisible = false
                }
                mAdapter.notifyDataSetChanged()

            }, { error: VolleyError ->
                error.printStackTrace()
                mErrorMessage.isVisible = true
                mRecyclerView.isVisible = false
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