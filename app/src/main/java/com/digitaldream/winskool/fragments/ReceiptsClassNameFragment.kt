package com.digitaldream.winskool.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.winskool.R
import com.digitaldream.winskool.activities.PaymentActivity
import com.digitaldream.winskool.adapters.OmItemClickListener
import com.digitaldream.winskool.adapters.ReceiptsClassNameAdapter
import com.digitaldream.winskool.config.DatabaseHelper
import com.digitaldream.winskool.dialog.OnInputListener
import com.digitaldream.winskool.dialog.TermlyFeeDialog
import com.digitaldream.winskool.models.ClassNameTable
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager

private const val ARG_LEVEL_ID = "levelId"
private const val ARG_LEVEL_NAME = "levelName"

class ReceiptsClassNameFragment : Fragment(), OmItemClickListener {

    private lateinit var mMainView: NestedScrollView
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mErrorMessage: TextView
    private lateinit var mLevelNameBtn: Button

    private var levelId: String? = null
    private var levelName: String? = null
    private var mClassList = mutableListOf<ClassNameTable>()
    private lateinit var mAdapter: ReceiptsClassNameAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            levelId = it.getString(ARG_LEVEL_ID)
            levelName = it.getString(ARG_LEVEL_NAME)
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(sLevelId: String, sLevelName: String) =
            ReceiptsClassNameFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_LEVEL_ID, sLevelId)
                    putString(ARG_LEVEL_NAME, sLevelName)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(
            R.layout.fragment_receipts_class_name, container,
            false
        )
        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        mMainView = view.findViewById(R.id.class_name_view)
        mRecyclerView = view.findViewById(R.id.class_name_recycler)
        mErrorMessage = view.findViewById(R.id.class_error_message)
        mLevelNameBtn = view.findViewById(R.id.level_name)

        toolbar.apply {
            title = "Select Class"
            setNavigationIcon(R.drawable.arrow_left)
            setNavigationOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        mLevelNameBtn.text = levelName

        getClassName(levelId!!)

        changeLevel()

        return view
    }

    private fun setLevelName() {
        mLevelNameBtn.text = levelName
    }

    private fun changeLevel() {
        mLevelNameBtn.setOnClickListener {
            TermlyFeeDialog(
                requireContext(),
                mLevelNameBtn.text.toString(),
                "receipt",
                object : OnInputListener {
                    override fun sendInput(input: String) {
                        levelName = input
                        setLevelName()
                    }

                    override fun sendLevelId(levelId: String) {
                        getClassName(levelId)
                    }
                },
            ).apply {
                setCancelable(true)
                show()
            }.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    private fun getClassName(sLevelId: String) {
        try {
            mClassList.clear()
            val databaseHelper =
                DatabaseHelper(requireContext())
            val mDao: Dao<ClassNameTable, Long> = DaoManager.createDao(
                databaseHelper.connectionSource, ClassNameTable::class.java
            )
            mClassList = mDao.queryBuilder().where().eq("level", sLevelId).query()

            if (mClassList.isEmpty()) {
                mMainView.isVisible = true
                mErrorMessage.isVisible = true
                mRecyclerView.isVisible = false
            } else {
                mAdapter = ReceiptsClassNameAdapter(mClassList, this)
                mRecyclerView.hasFixedSize()
                mRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                mRecyclerView.adapter = mAdapter
                mMainView.isVisible = true
                mErrorMessage.isVisible = false
                mRecyclerView.isVisible = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onItemClick(position: Int) {
        val classTable = mClassList[position]
        startActivity(
            Intent(context, PaymentActivity::class.java)
                .putExtra("classId", classTable.classId)
                .putExtra("class_name", classTable.className)
                .putExtra("from", "receipt_class_name")
        )

    }
}