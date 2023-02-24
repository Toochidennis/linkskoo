package com.digitaldream.winskool.utils

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.os.SystemClock
import android.util.DisplayMetrics
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.digitaldream.winskool.R
import org.achartengine.ChartFactory
import org.achartengine.GraphicalView
import org.achartengine.chart.BarChart
import org.achartengine.model.XYMultipleSeriesDataset
import org.achartengine.model.XYSeries
import org.achartengine.renderer.XYMultipleSeriesRenderer
import org.achartengine.renderer.XYSeriesRenderer
import java.io.*
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
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

    @JvmStatic
    @Throws(ParseException::class)
    fun dateConverter(date: String): String? {
        val format = "$date 00:00:00.000Z".replace(" ", "T")
        val simpleDateFormat = SimpleDateFormat(
            "yyyy-MM-dd" + "'T'HH" + ":mm:ss.SSS'Z'", Locale.US
        )
        val oldDate = simpleDateFormat.parse(format)!!
        return DateFormat.getDateInstance(DateFormat.FULL).format(oldDate)
    }

    private fun createBitMap(sView: View, sWidth: Int, sHeight: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(sWidth, sHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        sView.draw(canvas)
        return bitmap
    }

    private fun createPDF(sView: View, sActivity: Activity): PdfDocument {
        val displayMetrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        sActivity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels //1.4142
        val height = displayMetrics.heightPixels

        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(width, height, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint()

        var bitmap = createBitMap(sView, sView.width, sView.height)
        bitmap = Bitmap.createScaledBitmap(bitmap, sView.width, sView.height, true)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)

        pdfDocument.finishPage(page)

        return pdfDocument
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun notification(max: Int, sFile: File, sActivity: Activity) {
        val notificationManger: NotificationManagerCompat =
            NotificationManagerCompat.from(sActivity)

        if (ActivityCompat.checkSelfPermission(
                sActivity,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                sActivity, arrayOf(
                    Manifest.permission.POST_NOTIFICATIONS
                ),
                PackageManager.PERMISSION_GRANTED
            )
        } else if (ActivityCompat.checkSelfPermission(
                sActivity,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            val pendingIntent = PendingIntent.getActivity(
                sActivity,
                0, notificationIntent(sFile, sActivity), PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(
                sActivity, CHANNEL_ID
            ).apply {
                setSmallIcon(R.drawable.win_school)
                setContentTitle("Payment Receipt")
                setContentText("Download in progress")
                setContentIntent(pendingIntent)
                color = ContextCompat.getColor(sActivity, R.color.color_5)
                setProgress(max, 0, false)
                setOngoing(true)
                setOnlyAlertOnce(true)
                priority = NotificationCompat.PRIORITY_DEFAULT
            }

            notificationManger.notify(1, notification.build())

            Thread {
                SystemClock.sleep(1000)
                for (progress in 0..max step 10) {
                    notification.setProgress(max, progress, false)
                    notificationManger.notify(1, notification.build())
                    SystemClock.sleep(1000)
                }
                notification.setContentText("Download finished")
                    .setProgress(0, 0, false)
                    .setOngoing(false)
                notificationManger.notify(1, notification.build())
            }.start()
        }
    }

    private fun notificationIntent(sFile: File, sActivity: Activity): Intent {
        val intent = Intent(Intent.ACTION_VIEW)
        val uri = FileProvider.getUriForFile(
            sActivity, sActivity.packageName + "" +
                    ".provider", sFile
        )
        intent.setDataAndType(uri, "application/pdf")
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        return intent
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @JvmStatic
    fun downloadPDF(sView: View, sActivity: Activity) {

        val randomId = UUID.randomUUID().toString()
        try {
            val file = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .absolutePath + "/receipt$randomId.pdf"
            )
            createPDF(sView, sActivity).writeTo(FileOutputStream(file))
            val fileSize = (file.length() / 1024).toInt()
            notification(fileSize, file, sActivity)

        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(
                sActivity, "Something went wrong please try again!", Toast
                    .LENGTH_SHORT
            ).show()
        }
        createPDF(sView, sActivity).close()
    }

    @JvmStatic
    fun sharePDF(sView: View, sActivity: Activity) {

        val path = sActivity.cacheDir
        val output = File.createTempFile("receipt", ".pdf", path)

        try {
            createPDF(sView, sActivity).writeTo(FileOutputStream(output))
        } catch (e: IOException) {
            e.printStackTrace()
        }
        createPDF(sView, sActivity).close()

        val uri = FileProvider.getUriForFile(
            sActivity, sActivity.packageName + "" +
                    ".provider", output
        )

        ShareCompat.IntentBuilder(sActivity).apply {
            setType("application/pdf")
            setSubject("Share Pdf")
            addStream(uri)
            setChooserTitle("Share receipt")
            startChooser()
        }
    }

}