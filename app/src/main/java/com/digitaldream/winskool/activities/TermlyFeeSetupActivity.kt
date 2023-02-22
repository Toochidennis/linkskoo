package com.digitaldream.winskool.activities

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
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
import com.digitaldream.winskool.DatabaseHelper
import com.digitaldream.winskool.R
import com.digitaldream.winskool.adapters.TermFeeAdapter
import com.digitaldream.winskool.dialog.OnInputListener
import com.digitaldream.winskool.dialog.TermlyFeeDialog
import com.digitaldream.winskool.models.LevelTable
import com.digitaldream.winskool.models.TermFeesDataModel
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class TermlyFeeSetupActivity : AppCompatActivity(R.layout.activity_termly_fee_setup),
    OnInputListener {

    private var mLevel: String? = null
    private var mLevelName: String? = null
    private var mDb: String? = null
    private var mYear: String? = null
    private var mTerm: String? = null
    private var mLevelId: String? = null
    private var mLevelList: MutableList<LevelTable> = arrayListOf()

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: TermFeeAdapter
    private lateinit var mTermFeesList: MutableList<TermFeesDataModel>
    private lateinit var mRefreshLayout: SwipeRefreshLayout
    private lateinit var mErrorMessage: TextView
    private lateinit var mFeeTotal: TextView
    private lateinit var mLevelText: Button
    private lateinit var mSaveBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mToolbar: Toolbar = findViewById(R.id.toolbar)
        mSaveBtn = findViewById(R.id.pay_btn)
        mFeeTotal = findViewById(R.id.fee_total)
        mRecyclerView = findViewById(R.id.term_fee_recycler)
        mRefreshLayout = findViewById(R.id.swipeRefresh)
        mErrorMessage = findViewById(R.id.error_message)
        mLevelText = findViewById(R.id.level_name)

        mLevel = intent.getStringExtra("term_text")

        val sharedPreferences =
            getSharedPreferences("loginDetail", Context.MODE_PRIVATE)
        mYear = sharedPreferences.getString("school_year", "")
        mTerm = sharedPreferences.getString("term", "")
        mDb = sharedPreferences.getString("db", "")

        try {
            val previousYear = mYear!!.toInt() - 1

            val termText = when (mTerm) {
                "1" -> "1st Term"
                "2" -> "2nd Term"
                else -> "3rd Term"
            }

            mToolbar.apply {
                title = String.format(
                    Locale.getDefault(),
                    "%d/%s %s %s",
                    previousYear, mYear, termText, "Fees"
                )
                setNavigationIcon(R.drawable.arrow_left)
                setNavigationOnClickListener {
                    onBackPressedDispatcher.onBackPressed()
                }
            }

            mLevelText.text = mLevel

            mLevelId = getLevelId(mLevel!!.replace(" ", ""))

        } catch (e: Exception) {
            e.printStackTrace()
        }

        mTermFeesList = arrayListOf()

        mAdapter = TermFeeAdapter(this, mTermFeesList, mFeeTotal, mSaveBtn, mLevelId!!)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.hasFixedSize()
        mRecyclerView.isNestedScrollingEnabled = false
        mRecyclerView.adapter = mAdapter

        getTermFees(mLevelId!!)

        openDialog()

        mRefreshLayout.setColorSchemeColors(
            ContextCompat.getColor(
                this,
                R.color.color_5
            )
        )
        mRefreshLayout.setOnRefreshListener {
            mFeeTotal.text = getString(R.string.zero_balance)
            mRefreshLayout.isRefreshing = false
        }

    }

    private fun refreshData() {
        mTermFeesList.clear()
    }

    private fun getTermFees(sLevelId: String) {
        val progressFlower = ACProgressFlower.Builder(this)
            .direction(ACProgressConstant.DIRECT_CLOCKWISE)
            .textMarginTop(10)
            .fadeColor(ContextCompat.getColor((this as AppCompatActivity), R.color.color_5))
            .build()
        progressFlower.setCancelable(false)
        progressFlower.setCanceledOnTouchOutside(false)
        progressFlower.show()
        val url =
            Login.urlBase + "/manageTermFees.php?list=1&&level=$sLevelId&&term=$mTerm&&year=$mYear"
        val stringRequest: StringRequest = object : StringRequest(
            Method.GET,
            url,
            { response: String ->
                Log.i("response", response)
                progressFlower.dismiss()

                try {
                    val jsonObjects = JSONObject(response)
                    for (objects in jsonObjects.keys()) {
                        val jsonObject = jsonObjects.getJSONObject(objects)
                        val feeId = jsonObject.getString("fee_id")
                        val feeName = jsonObject.getString("fee_name")
                        val mandatory = jsonObject.getString("mandatory")
                        val feeAmount = jsonObject.getString("amount")

                        val termFeesModel = TermFeesDataModel()
                        termFeesModel.setFeeId(feeId.toInt())
                        termFeesModel.setFeeName(feeName)
                        termFeesModel.setFeeAmount(feeAmount.replace(".00", ""))
                        termFeesModel.setMandatory(mandatory)
                        mTermFeesList.add(termFeesModel)
                        mTermFeesList.sortBy { it.getFeeName() }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

                if (mTermFeesList.isEmpty()) {
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
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    private fun setLevelName() {
        mLevelText.text = mLevelName
    }

    private fun openDialog() {

        mLevelText.setOnClickListener {

            val termFeeDialog = TermlyFeeDialog(
                this@TermlyFeeSetupActivity,
                this@TermlyFeeSetupActivity,
                mLevelText.text.toString()
            )
            termFeeDialog.apply {
                setCancelable(true)
                show()
            }

            val window = termFeeDialog.window
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

        }
    }

    private fun getLevelId(sLevelName: String): String {
        var id = ""
        try {
            val databaseHelper = DatabaseHelper(this)
            val mDao: Dao<LevelTable, Long> = DaoManager.createDao(
                databaseHelper
                    .connectionSource, LevelTable::class.java
            )
            mLevelList = mDao.queryBuilder().where().eq("levelName", sLevelName).query()

            id = mLevelList[0].levelId

        } catch (e: Exception) {
            when (e) {
                is IndexOutOfBoundsException, is IllegalArgumentException -> e.printStackTrace()
                else -> throw e
            }
        }
        return id
    }

    override fun sendInput(input: String) {
        mLevelName = input
        setLevelName()
        mLevelId = getLevelId(mLevelName!!.replace(" ", ""))
        mTermFeesList.clear()
        getTermFees(mLevelId!!)
    }

}