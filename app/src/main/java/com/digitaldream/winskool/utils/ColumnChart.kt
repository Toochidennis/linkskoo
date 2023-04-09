package com.digitaldream.winskool.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.digitaldream.winskool.R
import com.digitaldream.winskool.models.ChartValue

@SuppressLint("SetJavaScriptEnabled")
class ColumnChart @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    WebView(context, attrs) {

    private lateinit var colorsArray: IntArray
    private var xAxisTitle: String? = null
    private var yAxisTitle: String? = null
    private var chartTitle: String? = null
    private var chartData: String? = null
    private val jsPath = "file:///android_asset/jsapi.js"


    init {
        val typedArray = context.obtainStyledAttributes(
            attrs, R.styleable.ColumnChart
        )

        try {
            chartTitle = typedArray.getString(R.styleable.ColumnChart_chartTitle)
            
            /*colorsArray = intArrayOf(
                typedArray.getColor(
                    R.styleable.GraphView_gradientStartColor,
                    ContextCompat.getColor(context, R.color.startColor)
                ),
                typedArray.getColor(
                    R.styleable.GraphView_gradientEndColor,
                    ContextCompat.getColor(context, R.color.endColor)
                )
            )*/

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            typedArray.recycle()
        }
    }


    fun setXAxisTitle(title: String) {
        xAxisTitle = title
    }

    fun setYAxisTitle(title: String) {
        yAxisTitle = title
    }

    private fun getXAxisTitle() = xAxisTitle

    private fun getYAxisTitle() = yAxisTitle

    fun setChartData(chartValues: ArrayList<ChartValue>) {
        val builder = StringBuilder()
        for (item in chartValues) {
            builder.append("[")
            builder.append(item.x).append(", ")
            builder.append(item.y)
            builder.append("],\n")
        }
        Log.d("data", builder.toString())
        chartData = builder.toString()
    }


    private fun init() {
        val content = " <html>\n" +
                "<head>\n" +
                "    <script type=\"text/javascript\" src=\"$jsPath\"></script>\n" +
                "    <script type=\"text/javascript\">\n" +
                "      google.charts.load('current', {'packages':['corechart']});\n" +
                "      google.charts.setOnLoadCallback(drawChart);\n" +
                "\n" +
                "      function drawChart() {\n" +
                "        var data = new google.visualization.DataTable();\n" +
                "        data.addColumn('string', 'Topping');\n" +
                "        data.addColumn('number', 'Slices');\n" +
                "        data.addRows([\n" +
                "        $chartData"
        "        ]);\n" +
                "\n" +
                "         var options = {\n" +
                "        title: \"$chartTitle\",\n" +
                "        hAxis: \"${getYAxisTitle()}\",\n"
        "        width: 600,\n" +
                "        height: 400,\n" +
                "        bar: {groupWidth: \"35%\"},\n" +
                "        legend: { position: \"none\" },\n" +
                "      };\n" +
                "      var chart = new google.visualization.ColumnChart(document.getElementById(\"columnchart_values\"));\n" +
                "      chart.draw(view, options);\n" +
                "    </script>\n" +
                "</head>\n" +
                "<body>\n" +
                "<div id=\"columnchart_values\" style=\"width: 600px; height: 400px;\"></div>\n" +
                "</body>\n" +
                "</html>\n"

        settings.apply {
            domStorageEnabled = true
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
        }
        requestFocusFromTouch()

        webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?,
            ): Boolean {
                view?.loadUrl(request?.url.toString())
                return false
            }
        }

        loadDataWithBaseURL("file:///android_asset/", content, "text/html", "utf-8", null)

    }

    fun loadChart() {
        init()
    }

}
