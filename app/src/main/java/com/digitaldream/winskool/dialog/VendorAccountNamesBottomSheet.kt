package com.digitaldream.winskool.dialog

import android.content.DialogInterface
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.winskool.R
import com.digitaldream.winskool.adapters.VendorAccountNamesAdapter
import com.digitaldream.winskool.interfaces.OnNameClickListener
import com.digitaldream.winskool.models.AccountSetupDataModel
import com.digitaldream.winskool.models.TimeFrameDataModel
import com.digitaldream.winskool.models.VendorModel
import com.digitaldream.winskool.utils.FunctionUtils.flipAnimation
import com.digitaldream.winskool.utils.FunctionUtils.requestToServer
import com.digitaldream.winskool.utils.VolleyCallback
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.json.JSONArray
import org.json.JSONObject

class VendorAccountNamesBottomSheet(
    private val sTimeFrameDataModel: TimeFrameDataModel,
    private val sFrom: String,
    private val sDismiss: () -> Unit
) : BottomSheetDialogFragment(), OnNameClickListener {

    private lateinit var mErrorMessage: TextView
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mTitle: TextView
    private lateinit var mErrorView: LinearLayout
    private lateinit var mRefreshBtn: Button
    private lateinit var mDoneBtn: Button
    private lateinit var mDismissBtn: ImageView

    private val mVendorList = mutableListOf<VendorModel>()
    private val mAccountList = mutableListOf<AccountSetupDataModel>()

    private val selectedItems = hashMapOf<String, String>()

    private var accountItemPosition = AccountSetupDataModel()
    private var vendorItemPosition = VendorModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_vendor_account_names, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.apply {
            mDismissBtn = findViewById(R.id.close_btn)
            mErrorMessage = findViewById(R.id.error_message)
            mRecyclerView = findViewById(R.id.recycler_view)
            mTitle = findViewById(R.id.title)
            mErrorView = findViewById(R.id.error_view)
            mRefreshBtn = findViewById(R.id.refresh_btn)
            mDoneBtn = findViewById(R.id.done_btn)
        }



        if (sFrom == "vendor") {
            vendorNames()
        } else {
            accountNames()
        }

        mDismissBtn.setOnClickListener { dismiss() }

//        closeCountBtn.setOnClickListener {
//            mTitleLayout.isVisible = true
//            mCountLayout.isVisible = false
//
//            sTimeFrameDataModel.vendorName = null
//            sTimeFrameDataModel.vendorId = null
//            sTimeFrameDataModel.accountId = null
//            sTimeFrameDataModel.accountName = null
//        }

    }

    private fun accountNames() {
        val url = "${getString(R.string.base_url)}/manageAccount.php?list=1"
        val hashMap = hashMapOf<String, String>()

        "Select Account".let { mTitle.text = it }
        mAccountList.clear()

        requestToServer(Request.Method.GET, url, requireContext(), hashMap,
            object : VolleyCallback {
                override fun onResponse(response: String) {
                    try {
                        if (response != "[]") {
                            JSONArray(response).run {
                                for (i in 0 until length()) {
                                    val jsonObject = getJSONObject(i)
                                    val id = jsonObject.getString("id")
                                    val accountName = jsonObject.getString("account_name")

                                    AccountSetupDataModel().apply {
                                        mId = id
                                        mAccountName = accountName
                                    }.let {
                                        mAccountList.apply {
                                            add(it)
                                            sortBy { sort -> sort.mAccountName }
                                        }
                                    }
                                }
                            }

                            VendorAccountNamesAdapter(
                                mAccountList,
                                null,
                                this@VendorAccountNamesBottomSheet
                            ).run {
                                mRecyclerView.apply {
                                    isAnimating
                                    isVisible = true
                                    hasFixedSize()
                                    layoutManager = LinearLayoutManager(requireContext())
                                    adapter = this@run
                                }
                            }

                            mErrorView.isVisible = false

                        } else {
                            mRecyclerView.isVisible = false
                            mErrorView.isVisible = true
                            mRefreshBtn.isVisible = false
                        }


                    } catch (e: Exception) {
                        e.printStackTrace()
                        mRecyclerView.isVisible = false
                        mErrorView.isVisible = true
                        mRefreshBtn.isVisible = false
                        mErrorMessage.text = getString(R.string.contact_developer)
                    }

                }

                override fun onError(error: VolleyError) {
                    mRecyclerView.isVisible = false
                    mErrorView.isVisible = true
                    mErrorMessage.text = getString(R.string.can_not_retrieve)
                }
            })


    }

    private fun vendorNames() {
        mVendorList.clear()
        val url = "${getString(R.string.base_url)}/manageVendor.php?list=2"
        val hashMap = hashMapOf<String, String>()

        "Select Vendor".let { mTitle.text = it }

        requestToServer(Request.Method.GET, url, requireActivity(), hashMap,
            object : VolleyCallback {
                override fun onResponse(response: String) {
                    try {
                        if (response != "[]") {
                            JSONArray(response).run {
                                for (i in 0 until length()) {
                                    val jsonObject = getJSONObject(i)
                                    val id = jsonObject.getString("id")
                                    val vendorName = jsonObject.getString("customername")

                                    VendorModel().apply {
                                        this.id = id
                                        this.customerName = vendorName
                                    }.let {
                                        mVendorList.apply {
                                            add(it)
                                            sortBy { sort -> sort.customerName }
                                        }
                                    }
                                }
                            }

                            VendorAccountNamesAdapter(
                                null,
                                mVendorList,
                                this@VendorAccountNamesBottomSheet
                            ).run {
                                mRecyclerView.apply {
                                    isAnimating
                                    isVisible = true
                                    hasFixedSize()
                                    layoutManager = LinearLayoutManager(requireContext())
                                    adapter = this@run
                                }
                            }

                            mErrorView.isVisible = false

                        } else {
                            mRecyclerView.isVisible = false
                            mErrorView.isVisible = true
                            mRefreshBtn.isVisible = false
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                        mRecyclerView.isVisible = false
                        mErrorView.isVisible = true
                        mRefreshBtn.isVisible = false
                        mErrorMessage.text = getString(R.string.contact_developer)
                    }
                }

                override fun onError(error: VolleyError) {
                    mRecyclerView.isVisible = false
                    mErrorView.isVisible = true
                    mErrorMessage.text = getString(R.string.can_not_retrieve)
                }
            })
    }


    override fun onDismiss(dialog: DialogInterface) {
        sDismiss()
    }


    override fun onNameClick(holder: VendorAccountNamesAdapter.ViewHolder) {

        holder.itemView.setOnClickListener {

            if (mAccountList.isNotEmpty()) {
                accountItemPosition = mAccountList[holder.adapterPosition]
            } else {
                vendorItemPosition = mVendorList[holder.adapterPosition]
            }


            if (selectedItems.contains(accountItemPosition.mAccountId) ||
                selectedItems.contains(vendorItemPosition.customerId)
            ) {

                flipAnimation(
                    requireContext(),
                    holder.itemTextLayout,
                    holder.itemImageLayout,
                    "left"
                )

                it.setBackgroundColor(Color.TRANSPARENT)

                selectedItems.remove(
                    accountItemPosition.mId ?: vendorItemPosition.id
                )

                if (selectedItems.isEmpty()) {
                    mDoneBtn.isVisible = false
                    mDismissBtn.isVisible = true
                }

            } else {

                if (selectedItems.size == 3) {
                    Toast.makeText(
                        requireActivity(), "Only 3 items can be selected", Toast
                            .LENGTH_SHORT
                    ).show()

                } else {
                    flipAnimation(
                        requireContext(),
                        holder.itemTextLayout,
                        holder.itemImageLayout,
                        "right"
                    )

                    it.setBackgroundColor(Color.GRAY)
                    mDoneBtn.isVisible = true
                    mDismissBtn.isVisible = false

                    selectedItems.apply {
                        put(
                            accountItemPosition.mId ?: vendorItemPosition.id,
                            accountItemPosition.mAccountName ?: vendorItemPosition.customerName
                        )
                    }

                }
            }


        }

        performButtonClick(selectedItems)

    }


    private fun performButtonClick(selectedItem: HashMap<String, String>) {

        val jsonArray = JSONArray()

        mDoneBtn.setOnClickListener {

            selectedItem.forEach { (key, value) ->

                JSONObject().apply {

                    if (mAccountList.isNotEmpty()) {
                        put("id", key)
                        put("account_name", value)
                    } else {
                        put("id", key)
                        put("customername", value)
                    }

                    jsonArray.put(this)
                }

            }


            println("names: $jsonArray")
        }

    }


}