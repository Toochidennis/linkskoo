package com.digitaldream.winskool.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.winskool.R
import com.digitaldream.winskool.adapters.DialogClassNameAdapter
import com.digitaldream.winskool.adapters.OnClassClickListener
import com.digitaldream.winskool.adapters.OnItemClickListener
import com.digitaldream.winskool.adapters.TermFeeDialogAdapter
import com.digitaldream.winskool.config.DatabaseHelper
import com.digitaldream.winskool.models.ClassNameTable
import com.digitaldream.winskool.models.LevelTable
import com.digitaldream.winskool.models.TimeFrameData
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager


class ReceiptsFilterFragment(
    private val sTimeFrameData: TimeFrameData,
    private val sDismiss: () -> Unit
) : Fragment(), OnItemClickListener, OnClassClickListener {

    private lateinit var mLevelRecyclerView: RecyclerView
    private lateinit var mClassRecyclerView: RecyclerView
    private lateinit var mClassTitle: TextView
    private lateinit var mLevelTitle: TextView
    private lateinit var mErrorMessage: TextView

    private var mLevelList = mutableListOf<LevelTable>()
    private var mClassList = mutableListOf<ClassNameTable>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_receipts_filter, container, false)

        mLevelRecyclerView = view.findViewById(R.id.level_recycler)
        mClassRecyclerView = view.findViewById(R.id.class_recycler)
        mClassTitle = view.findViewById(R.id.class_title)
        mLevelTitle = view.findViewById(R.id.level_title)
        mErrorMessage = view.findViewById(R.id.error_message)

        getLevelName()

        return view
    }


    private fun getLevelName() {
        try {
            val databaseHelper =
                DatabaseHelper(context)
            val mDao: Dao<LevelTable, Long> = DaoManager.createDao(
                databaseHelper.connectionSource, LevelTable::class.java
            )

            mLevelList = mDao.queryForAll()
            mLevelList.sortBy { it.levelName }

            if (mLevelList.isEmpty()) {
                mErrorMessage.isVisible = true
            } else {
                TermFeeDialogAdapter(requireContext(), mLevelList, this).let {
                    mLevelRecyclerView.apply {
                        hasFixedSize()
                        layoutManager = GridLayoutManager(context, 2)
                        adapter = it
                        isVisible = true
                        isAnimating
                    }

                    mErrorMessage.isVisible = false
                    mLevelTitle.isVisible = true
                }

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun getClassName(sLevelId: String) {
        try {
            mClassList.clear()

            val databaseHelper =
                DatabaseHelper(context)
            val mDao: Dao<ClassNameTable, Long> = DaoManager.createDao(
                databaseHelper.connectionSource, ClassNameTable::class.java
            )
            mClassList = mDao.queryBuilder().where().eq("level", sLevelId).query()

            if (mClassList.isEmpty()) {
                mErrorMessage.isVisible = true
                mClassRecyclerView.isVisible = false
                mClassTitle.isVisible = false

            } else {
                DialogClassNameAdapter(mClassList, this).let {
                    mClassRecyclerView.apply {
                        hasFixedSize()
                        isVisible = true
                        isAnimating
                        layoutManager = GridLayoutManager(context, 2)
                        adapter = it
                    }
                }

                mErrorMessage.isVisible = false
                mClassTitle.isVisible = true

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onItemClick(position: Int) {
        val levelTable = mLevelList[position]

        getClassName(levelTable.levelId)

    }

    override fun onClassClick(position: Int) {
        val classTable = mClassList[position]
        sTimeFrameData.others = classTable.className
        sDismiss()

    }

}