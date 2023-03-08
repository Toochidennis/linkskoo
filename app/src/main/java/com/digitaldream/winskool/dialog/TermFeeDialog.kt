package com.digitaldream.winskool.dialog

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.winskool.R
import com.digitaldream.winskool.activities.TermlyFeeSetupActivity
import com.digitaldream.winskool.adapters.OmItemClickListener
import com.digitaldream.winskool.adapters.OnClassClickListener
import com.digitaldream.winskool.adapters.ReceiptsClassNameAdapter
import com.digitaldream.winskool.adapters.TermFeeDialogAdapter
import com.digitaldream.winskool.config.DatabaseHelper
import com.digitaldream.winskool.models.ClassNameTable
import com.digitaldream.winskool.models.LevelTable
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager

class TermFeeDialog(
    sContext: Context,
    private val sFrom: String,
) : Dialog(sContext), OmItemClickListener, OnClassClickListener {

    private var mLevelList = mutableListOf<LevelTable>()
    private var mClassList = mutableListOf<ClassNameTable>()
    private lateinit var mAdapter: ReceiptsClassNameAdapter

    private lateinit var mLevelRecyclerView: RecyclerView
    private lateinit var mClassRecyclerView: RecyclerView
    private lateinit var mLeftAnimation: Animation
    private lateinit var mRightAnimation: Animation
    private lateinit var mTitle: TextView
    private lateinit var mBackBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.attributes.windowAnimations = R.style.DialogAnimation
        window!!.setGravity(Gravity.BOTTOM)
        setContentView(R.layout.dialog_fee_term)

        mLevelRecyclerView = findViewById(R.id.level_recycler)
        mClassRecyclerView = findViewById(R.id.class_recycler)
        mTitle = findViewById(R.id.title)
        mBackBtn = findViewById(R.id.back_btn)

        mLeftAnimation = AnimationUtils.loadAnimation(context, R.anim.move_left)
        mRightAnimation = AnimationUtils.loadAnimation(context, R.anim.move_right)

        getLevelName()
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
            val mAdapter = TermFeeDialogAdapter(context, mLevelList, this)
            mLevelRecyclerView.hasFixedSize()
            mLevelRecyclerView.layoutManager = GridLayoutManager(context, 2)
            mLevelRecyclerView.adapter = mAdapter

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getClassName(sLevelId: String) {
        try {
            val databaseHelper =
                DatabaseHelper(context)
            val mDao: Dao<ClassNameTable, Long> = DaoManager.createDao(
                databaseHelper.connectionSource, ClassNameTable::class.java
            )
            mClassList = mDao.queryBuilder().where().eq("level", sLevelId).query()

            if (mClassList.isEmpty()) {
                mClassRecyclerView.isVisible = false
            } else {
                mAdapter = ReceiptsClassNameAdapter(mClassList, this)
                mClassRecyclerView.hasFixedSize()
                mClassRecyclerView.layoutManager = LinearLayoutManager(context)
                mClassRecyclerView.adapter = mAdapter
                mClassRecyclerView.isVisible = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onItemClick(position: Int) {
        val levelTable = mLevelList[position]

        if (sFrom == "term") {
            context.startActivity(
                Intent(context, TermlyFeeSetupActivity()::class.java)
                    .putExtra("level_name", levelTable.levelName)
                    .putExtra("level_id", levelTable.levelId)
            )
            dismiss()
        } else {
            getClassName(levelTable.levelId)

            mLevelRecyclerView.startAnimation(mLeftAnimation)
            mLevelRecyclerView.animate().setListener(object : AnimatorListenerAdapter() {

                override fun onAnimationStart(animation: Animator) {
                    mClassRecyclerView.isVisible = true
                    super.onAnimationStart(animation)
                }

                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    mLevelRecyclerView.isVisible = false
                    mLevelRecyclerView.animate().setListener(null)
                }
            })

            mClassRecyclerView.startAnimation(mRightAnimation)
            mClassRecyclerView.animate().setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    super.onAnimationStart(animation)
                    mClassRecyclerView.isVisible = true
                    mClassRecyclerView.animate().setListener(null)
                }
            })

        }

    }

    override fun onClassClick(position: Int) {
        val classTable = mClassList[position]

    }

}
/*
context.startActivity(
Intent(context, PaymentActivity().javaClass)
.putExtra("levelId", levelTable.levelId)
.putExtra("level_name", levelTable.levelName)
.putExtra("from", "add_receipt")
)
*/
