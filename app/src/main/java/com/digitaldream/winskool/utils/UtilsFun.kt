package com.digitaldream.winskool.utils

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.widget.ProgressBar
import android.widget.TextView
import org.achartengine.ChartFactory
import org.achartengine.GraphicalView
import org.achartengine.chart.BarChart
import org.achartengine.model.XYMultipleSeriesDataset
import org.achartengine.model.XYSeries
import org.achartengine.renderer.XYMultipleSeriesRenderer
import org.achartengine.renderer.XYSeriesRenderer
import java.text.DecimalFormat
import java.util.*

object UtilsFun {
    //var counter = 0

    @JvmStatic
    fun capitaliseFirstLetter(sS: String): String {
        val strings = sS.lowercase(Locale.getDefault()).split(" ".toRegex()).toTypedArray()
        val stringBuilder = StringBuilder()
        for (letter in strings) {
            try {
                val words =
                    letter.substring(0, 1).uppercase(Locale.getDefault()) +
                            letter.substring(1).lowercase(Locale.getDefault())
                stringBuilder.append(words).append(" ")
            } catch (sE: Exception) {
                sE.printStackTrace()
            }
        }
        return stringBuilder.toString()
    }

    fun abbreviate(sS: String): String {
        val strings = sS.lowercase(Locale.getDefault()).split(" ".toRegex()).toTypedArray()
        val stringBuilder = StringBuilder()
        for (letter in strings) {
            try {
                val words = letter.substring(0, 1).uppercase(Locale.getDefault())
                stringBuilder.append(words)
            } catch (sE: Exception) {
                sE.printStackTrace()
            }
        }
        return stringBuilder.toString()
    }

    fun setColor(): Int {
        val random = Random()
        return Color.argb(
            255, random.nextInt(256), random.nextInt(256),
            random.nextInt(256)
        )
    }

    /*    public static CountDownTimer startCountDown(ProgressBar sProgressBar,
                                                int sI,
                                      TextView sTextView) {

        return new CountDownTimer(5 * 1000, 1) {
            @Override
            public void onTick(long sL) {

                counter += 1;
                if (counter < sI + 1) {
                    sProgressBar.setProgress(counter);
                    sTextView.setText(counter + "%");
                    sProgressBar.setMax(100);
                }

            }

            @Override
            public void onFinish() {

            }
        };

    }*/
    @JvmStatic
    fun animateObject(sProgressBar: ProgressBar, sTextView: TextView, sI: Int) {
        ObjectAnimator.ofInt(sProgressBar, "progress", sI)
            .setDuration(1000)
            .start()
        val animator = ValueAnimator.ofInt(0, sI)
        animator.duration = 1000
        animator.addUpdateListener {
            sTextView.text = String.format("%s%%", animator.animatedValue)
        }
        animator.start()
    }

    @JvmStatic
    fun drawGraph(sValues: Array<Int>, sContext: Context): GraphicalView {

        val mMonth = arrayOf(
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        )
        val graphLength = intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)

        val graphicalView: GraphicalView

        val series = XYSeries("Income")

        for (i in graphLength.indices)
            series.add(graphLength[i].toDouble(), sValues[i].toDouble())

        val dataset = XYMultipleSeriesDataset()
        dataset.addSeries(series)

        val seriesRenderer = XYSeriesRenderer()
        seriesRenderer.color = Color.WHITE
        // seriesRenderer.isFillPoints = true
        seriesRenderer.isDisplayChartValues = true
        seriesRenderer.chartValuesTextSize = 20f

        val multipleRenderer = XYMultipleSeriesRenderer()
        multipleRenderer.xLabels = 0
        multipleRenderer.yLabels = 0
        multipleRenderer.xTitle = "Year 2023"
        multipleRenderer.isPanEnabled = false
        multipleRenderer.gridLineWidth = 1f
        multipleRenderer.setShowGrid(true)
        multipleRenderer.setGridColor(Color.WHITE)
        multipleRenderer.barSpacing = .5
        multipleRenderer.marginsColor = Color.parseColor("#130C6B")
        multipleRenderer.isZoomEnabled = false
        multipleRenderer.backgroundColor = Color.parseColor("#130C6B")
        multipleRenderer.isApplyBackgroundColor = true
        multipleRenderer.labelsColor = Color.WHITE
        multipleRenderer.labelsTextSize = 20f
        multipleRenderer.addSeriesRenderer(seriesRenderer)

        for (i in graphLength.indices)
            multipleRenderer.addXTextLabel(graphLength[i].toDouble(), mMonth[i])

        graphicalView = ChartFactory.getBarChartView(
            sContext, dataset, multipleRenderer,
            BarChart.Type.DEFAULT
        )

        return graphicalView
    }

    @JvmStatic
    fun currencyFormat(number: Double): String {
        val formatter = DecimalFormat("###,###,##0.00")
        return formatter.format(number)
    }


}