package com.digitaldream.winskool.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.winskool.R
import com.digitaldream.winskool.adapters.FilterLevelClassAdapter
import com.digitaldream.winskool.adapters.OnItemClickListener
import com.digitaldream.winskool.config.DatabaseHelper
import com.digitaldream.winskool.models.ClassNameTable
import com.digitaldream.winskool.models.LevelTable
import com.digitaldream.winskool.models.TimeFrameData
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager

class FilterLevelClassDialog(
    private val timeFrameData: TimeFrameData,
    private val sFrom: String,
    private val sDismiss: () -> Unit
) : BottomSheetDialogFragment(), OnItemClickListener {


    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mTitle: TextView
    private lateinit var mErrorMessage: TextView

    private var mLevelList = mutableListOf<LevelTable>()
    private var mClassList = mutableListOf<ClassNameTable>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.dialog_filter_level_class, container, false)

        val dismissBtn: ImageView = view.findViewById(R.id.close_btn)
        mRecyclerView = view.findViewById(R.id.recycler_view)
        mTitle = view.findViewById(R.id.title)
        mErrorMessage = view.findViewById(R.id.error_message)


        if (sFrom == "level") {
            getLevelName()
        } else {
            getClassName()
        }

        dismissBtn.setOnClickListener { dismiss() }


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

            println("Level list item ${mLevelList[0].levelName}")

            if (mLevelList.isEmpty()) {
                mErrorMessage.isVisible = true
                mRecyclerView.isVisible = false

            } else {
                FilterLevelClassAdapter(
                    sLevelList = mLevelList,
                    sClassList = null,
                    this
                ).let {
                    mRecyclerView.apply {
                        isVisible = true
                        isAnimating
                        layoutManager = LinearLayoutManager(context)
                        hasFixedSize()
                        adapter = it
                    }

                }
                mTitle.text = getString(R.string.select_level)
                mErrorMessage.isVisible = false

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun getClassName() {
        try {
            mClassList.clear()

            val databaseHelper =
                DatabaseHelper(context)
            val mDao: Dao<ClassNameTable, Long> = DaoManager.createDao(
                databaseHelper.connectionSource, ClassNameTable::class.java
            )
            mClassList = mDao.queryForAll()
            mClassList.sortBy { it.className }

            if (mClassList.isEmpty()) {
                mErrorMessage.isVisible = true
                mRecyclerView.isVisible = false

            } else {
                FilterLevelClassAdapter(
                    sLevelList = null,
                    sClassList = mClassList,
                    this
                ).let {
                    mRecyclerView.apply {
                        isVisible = true
                        isAnimating
                        layoutManager = LinearLayoutManager(context)
                        hasFixedSize()
                        adapter = it
                    }

                }
                mTitle.text = getString(R.string.select_class)
                mErrorMessage.isVisible = false

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onItemClick(position: Int) {
        if (sFrom == "level") {
            timeFrameData.levelId = mLevelList[position].levelId
            timeFrameData.levelName = mLevelList[position].levelName

            timeFrameData.className = null
            timeFrameData.classId = null
        } else {
            timeFrameData.classId = mClassList[position].classId
            timeFrameData.className = mClassList[position].className

            timeFrameData.levelName = null
            timeFrameData.levelId = null
        }

        dismiss()
    }

    override fun onDismiss(dialog: DialogInterface) {
        sDismiss()
    }


}