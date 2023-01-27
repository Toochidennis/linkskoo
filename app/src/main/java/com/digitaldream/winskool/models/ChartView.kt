package com.digitaldream.winskool.models

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import com.digitaldream.winskool.utils.ChartUtils
import kotlin.math.abs

class ChartView(sContext: Context, attributeSet: AttributeSet) : View(
    sContext, attributeSet) {
    private var percentList: ArrayList<Float>
    private var targetPercentList: ArrayList<Float>? = null
    private var textPaint: Paint
    private var backgroundPaint: Paint
    private var foregroundPaint: Paint
    private var barRect: Rect
    private var fillRect: Rect
    private var barWidth = 0
    private var bottomTextDescent = 0
    private val autoSetWidth = true
    private var topMargin = 0
    private var bottomTextHeight = 0
    private var bottomTextList = ArrayList<String>()
    private var miniBarWidth = 0
    private var barSideMargin = 0
    private var textTopMargin = 0
    private val textColor: Int = Color.parseColor("#9B9A9B")
    private val backgroundColor: Int = Color.parseColor("#F6F6F6")
    private val foregroundColor: Int = Color.parseColor("#FC496D")

    init {
        backgroundPaint = Paint().apply {
            isAntiAlias = true
            color = backgroundColor
        }

        foregroundPaint = Paint().apply {
            isAntiAlias = true
            color = foregroundColor
        }

        barRect = Rect()
        fillRect = Rect()

        topMargin = ChartUtils.deptPixel(sContext, 5f)
        barWidth = ChartUtils.deptPixel(sContext, 22f)
        miniBarWidth = ChartUtils.deptPixel(sContext, 22f)
        barSideMargin = ChartUtils.deptPixel(sContext, 22f)
        textTopMargin = ChartUtils.deptPixel(sContext, 5f)

        textPaint = Paint().apply {
            isAntiAlias = true
            color = textColor
            textSize = ChartUtils.scaledPixel(sContext, 15f).toFloat()
            textAlign = Paint.Align.CENTER
        }

        percentList = arrayListOf()
    }


    private val animator: Runnable = object : Runnable {
        override fun run() {
            var needNewFrame = false
            for (i in targetPercentList!!.indices) {
                if (percentList[i] < targetPercentList!![i]) {
                    percentList[i] = percentList[i] + 0.02f
                    needNewFrame = true
                } else if (percentList[i] > targetPercentList!![i]) {
                    percentList[i] = percentList[i] - 0.02f
                    needNewFrame = true
                }
                if (abs(targetPercentList!![i] - percentList[i]) < 0.02f) {
                    percentList[i] = targetPercentList!![i]
                }
            }

            if (needNewFrame) {
                postDelayed(this, 20)
            }
            invalidate()
        }

    }


    fun bottomList(sStringList: ArrayList<String>) {

        bottomTextList = sStringList
        val rect = Rect()
        barWidth = miniBarWidth

        for (letter in bottomTextList) {
            textPaint.getTextBounds(letter, 0, letter.length, rect)
            if (bottomTextHeight < rect.height()) {
                bottomTextHeight = rect.height()
            }
            if (autoSetWidth && (barWidth < rect.width())) {
                barWidth = rect.width()
            }
            if (bottomTextDescent < (abs(rect.bottom))) {
                bottomTextDescent = abs(rect.bottom)
            }
        }
        minimumWidth = 2
        postInvalidate()

    }

    fun setDataList(integerList: ArrayList<Int>, max: Int) {
        targetPercentList = arrayListOf()
        var max1 = max
        val temp: Int

        if (max1 == 0) max1 = 1

        for (i in integerList) {
            targetPercentList?.add((1 - i / max1).toFloat())
        }

        if (percentList.isEmpty() || percentList.size <
            targetPercentList!!.size
        ) {
            temp = targetPercentList!!.size - percentList.size
            for (i in 0 until temp) {
                percentList.add(1f)
            }
        } else if (percentList.size > targetPercentList!!.size) {
            temp = percentList.size - targetPercentList!!.size
            for (i in 0 until temp) {
                percentList.removeAt(percentList.size - 1)
            }
        }

        minimumWidth = 2
        removeCallbacks(animator)
        post(animator)

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        var value = 1

        if (percentList.isNotEmpty()) {
            for (i in percentList) {
                barRect.set(barSideMargin * value + barWidth * (value - 1),
                    topMargin, (barSideMargin + barWidth) * value, height
                            - bottomTextHeight - textTopMargin)

                canvas.drawRect(barRect, backgroundPaint)

                fillRect.set(barSideMargin * value + barWidth * (value - 1),
                    topMargin + ((height - topMargin - bottomTextHeight -
                            textTopMargin) * percentList[value - 1]).toInt(),
                    (barSideMargin + barWidth) * value, height -
                            bottomTextHeight - textTopMargin)

                canvas.drawRect(barRect, foregroundPaint)

                value++
            }
        }

        if (bottomTextList.isNotEmpty()) {
            value = 1

            for (letter in bottomTextList) {
                    canvas.drawText(letter,
                        (barSideMargin * value + barWidth * (value - 1) +
                                barWidth / 2).toFloat(),
                        (height - bottomTextDescent).toFloat(), textPaint)


                value++
            }

        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val mViewWidth: Int = measureWidth(widthMeasureSpec)
        val mViewHeight: Int = measureHeight(heightMeasureSpec)
        setMeasuredDimension(mViewWidth, mViewHeight)
    }

    private fun measureWidth(measureSpec: Int): Int {
        var preferred = 0
        if (bottomTextList.isNotEmpty()) {
            preferred = bottomTextList.size * (barWidth + barSideMargin)
        }
        return getMeasurement(measureSpec, preferred)
    }


    private fun measureHeight(measureSpec: Int): Int {
        val preferred = 222
        return getMeasurement(measureSpec, preferred)
    }


    private fun getMeasurement(measureSpec: Int, preferred: Int): Int {

        val specSize = MeasureSpec.getSize(measureSpec)
        val measurement: Int = when (MeasureSpec.getMode(measureSpec)) {
            MeasureSpec.EXACTLY -> specSize
            MeasureSpec.AT_MOST -> preferred.coerceAtMost(specSize)
            else -> preferred
        }
        return measurement
    }

}
