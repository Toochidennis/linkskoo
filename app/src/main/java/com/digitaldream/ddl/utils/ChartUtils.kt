package com.digitaldream.ddl.utils

import android.content.Context

class ChartUtils {

    companion object {

        fun deptPixel(sContext: Context, deptValue: Float): Int {
            val scale = sContext.resources.displayMetrics.density
            return (deptValue * scale + 0.5f).toInt()
        }

        fun pixelDept(sContext: Context, pixelValue: Float): Int {
            val scale = sContext.resources.displayMetrics.density
            return (pixelValue / scale + 0.5f).toInt()
        }

        fun scaledPixel(sContext: Context, scaleValue: Float): Int {
            val fontScale = sContext.resources.displayMetrics.scaledDensity
            return (scaleValue * fontScale + 0.5f).toInt()
        }
    }
}