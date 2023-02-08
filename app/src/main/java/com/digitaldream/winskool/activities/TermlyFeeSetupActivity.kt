package com.digitaldream.winskool.activities

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.core.view.isGone
import androidx.core.view.isVisible
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
import com.digitaldream.winskool.adapters.TermFeeAdapter
import com.digitaldream.winskool.dialog.TermlyFeeDialog
import com.digitaldream.winskool.models.FeeTypeModel
import org.json.JSONArray
import java.util.*


class TermlyFeeSetupActivity : AppCompatActivity(R.layout.activity_termly_fee_setup),
    TermlyFeeDialog.OnInputListener {

    private var mLevel: String? = null
    private var mLevelName: String? = null
    private var mDb: String? = null

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: TermFeeAdapter
    private lateinit var mFeeNameList: MutableList<FeeTypeModel>
    private lateinit var mRefreshLayout: SwipeRefreshLayout
    private lateinit var mErrorMessage: TextView
    private lateinit var mLevelText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val backBtn: ImageView = findViewById(R.id.back_btn)
        val saveBtn: ImageView = findViewById(R.id.save_btn)
        val title: TextView = findViewById(R.id.title)
        val mFeeTotal: TextView = findViewById(R.id.fee_total)
        mRecyclerView = findViewById(R.id.term_fee_recycler)
        mRefreshLayout = findViewById(R.id.swipeRefresh)
        mErrorMessage = findViewById(R.id.error_message)
        mLevelText = findViewById(R.id.level_name)

        mLevel = intent.getStringExtra("term_text")

        val sharedPreferences =
            getSharedPreferences("loginDetail", Context.MODE_PRIVATE)
        val year = sharedPreferences.getString("school_year", "")
        val term = sharedPreferences.getString("term", "")
        mDb = sharedPreferences.getString("db", "")

        backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        try {
            val previousYear = year!!.toInt() - 1

            val termText = when (term) {
                "1" -> "1st Term"
                "2" -> "2nd Term"
                else -> "3rd Term"
            }

            title.text = String.format(
                Locale.getDefault(),
                "%d/%s %s %s",
                previousYear, year, termText, "Fees"
            )

            mLevelText.setOnClickListener {
                val termFeeDialog = TermlyFeeDialog(this, this)
                termFeeDialog.apply {
                    setCancelable(false)
                    show()
                }

                val window = termFeeDialog.window
                window?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }

            mLevelText.setText(mLevel)

        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }

        mFeeNameList = arrayListOf()

        mAdapter = TermFeeAdapter(this, mFeeNameList, mFeeTotal)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.hasFixedSize()
        mRecyclerView.isNestedScrollingEnabled = false
        mRecyclerView.adapter = mAdapter

        getFeeName()

        mRefreshLayout.setColorSchemeColors(
            ContextCompat.getColor(
                this,
                R.color.color_5
            )
        )
        mRefreshLayout.setOnRefreshListener {
            refreshData()
            mFeeTotal.text = getString(R.string.zero_balance)
            mRefreshLayout.isRefreshing = false
        }

    }

    private fun refreshData() {
        mFeeNameList.clear()
        getFeeName()
    }

    private fun setUpMenu() {
        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.save_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.save -> {
                        Toast.makeText(this@TermlyFeeSetupActivity, "Saved", Toast.LENGTH_SHORT)
                            .show()
                        true
                    }
                    android.R.id.home -> {
                        onBackPressedDispatcher.onBackPressed()
                        return true
                    }
                    else -> false
                }
            }
        })
    }

    private fun getFeeName() {

        val progressFlower = ACProgressFlower.Builder(this)
            .direction(ACProgressConstant.DIRECT_CLOCKWISE)
            .textMarginTop(10)
            .fadeColor(ContextCompat.getColor((this as AppCompatActivity), R.color.color_5))
            .build()
        progressFlower.setCancelable(false)
        progressFlower.setCanceledOnTouchOutside(false)
        progressFlower.show()
        val url = Login.urlBase + "/manageFees.php?list=1"
        val stringRequest: StringRequest = object : StringRequest(
            Method.GET,
            url,
            { response: String ->
                Log.i("response", response)
                progressFlower.dismiss()

                val jsonArray = JSONArray(response)
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val feeId = jsonObject.getString("id")
                    val feeName = jsonObject.getString("fee_name")

                    val feeTypeModel = FeeTypeModel()
                    feeTypeModel.setFeeId(feeId.toInt())
                    feeTypeModel.setFeeName(feeName)

                    mFeeNameList.add(feeTypeModel)
                }

                if (mFeeNameList.isEmpty()) {
                    mErrorMessage.isVisible = true
                    mErrorMessage.text = getString(R.string.nothing_to_show)
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
                progressFlower.dismiss()
            }) {
            override fun getParams(): Map<String, String> {
                val stringMap: MutableMap<String, String> = HashMap()
                stringMap["_db"] = mDb!!
                return stringMap
            }
        }
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    private fun setLevelName() {
        mLevelText.setText(mLevelName)
    }

    override fun sendInput(input: String) {
        mLevelName = input
        setLevelName()
    }


}