package com.digitaldream.winskool.fragments

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.digitaldream.winskool.R
import com.digitaldream.winskool.dialog.DownloadReceiptDialog
import com.digitaldream.winskool.dialog.OnInputListener
import com.digitaldream.winskool.utils.CHANNEL_ID_1
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class TransactionReceiptFragment : Fragment(), OnInputListener {

    private var param1: String? = null
    private var param2: String? = null
    private lateinit var notificationManger: NotificationManagerCompat

    private lateinit var mReceiptCard: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(
                Manifest.permission
                    .WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE,
            ),
            PackageManager.PERMISSION_GRANTED
        )

        notificationManger = NotificationManagerCompat.from(requireContext())
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TransactionReceiptFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(
            R.layout.fragment_transaction_receipt,
            container, false
        )

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)

        toolbar.apply {
            setNavigationIcon(R.drawable.arrow_left)
            title = "Transaction details"
            setNavigationOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        }

        val shareBtn = view.findViewById<CardView>(R.id.share)
        mReceiptCard = view.findViewById(R.id.receipt_card)

        shareBtn.setOnClickListener {
            val downloadDialog = DownloadReceiptDialog(requireContext(), this)
            downloadDialog.setCancelable(true)
            downloadDialog.show()
            val window = downloadDialog.window
            window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        return view
    }

    private fun createBitMap(sView: View, sWidth: Int, sHeight: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(sWidth, sHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        sView.draw(canvas)
        return bitmap
    }

    private fun createPDF(): PdfDocument {
        val displayMetrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels //1.4142
        val height = displayMetrics.heightPixels

        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(width, height, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint()

        var bitmap = createBitMap(mReceiptCard, mReceiptCard.width, mReceiptCard.height)
        bitmap = Bitmap.createScaledBitmap(bitmap, mReceiptCard.width, mReceiptCard.height, true)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)

        pdfDocument.finishPage(page)

        return pdfDocument
    }

    private fun downloadPDF() {
        val randomId = UUID.randomUUID().toString()

        try {
            val file = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .absolutePath + "/receipt$randomId.pdf"
            )
            createPDF().writeTo(FileOutputStream(file))
            val fileSize = (file.length() / 1024).toInt()
            notification(fileSize, file)

        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(
                requireContext(), "Something went wrong please try again!", Toast
                    .LENGTH_SHORT
            ).show()
        }
        createPDF().close()
    }

    private fun sharePDF() {

        val path = requireContext().cacheDir
        val output = File.createTempFile("receipt", ".pdf", path)

        try {
            createPDF().writeTo(FileOutputStream(output))
        } catch (e: IOException) {
            e.printStackTrace()
        }
        createPDF().close()

        val uri = FileProvider.getUriForFile(
            requireContext(), requireActivity().packageName + "" +
                    ".provider", output
        )

        ShareCompat.IntentBuilder(requireContext()).apply {
            setType("application/pdf")
            setSubject("Share Pdf")
            addStream(uri)
            setChooserTitle("Share receipt")
            startChooser()
        }
    }

    private fun notification(max: Int, sFile: File) {

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
                    Manifest.permission.POST_NOTIFICATIONS
                ),
                PackageManager.PERMISSION_GRANTED
            )
        } else if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            val pendingIntent = PendingIntent.getActivity(
                requireContext(),
                0, notificationIntent(sFile), PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(
                requireContext(), CHANNEL_ID_1
            ).apply {
                setSmallIcon(R.drawable.win_school)
                setContentTitle("Payment Receipt")
                setContentText("Download in progress")
                setContentIntent(pendingIntent)
                color = ContextCompat.getColor(requireContext(), R.color.color_5)
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

    private fun notificationIntent(sFile: File): Intent {
        val intent = Intent(Intent.ACTION_VIEW)
        val uri = FileProvider.getUriForFile(
            requireContext(), requireActivity().packageName + "" +
                    ".provider", sFile
        )
        intent.setDataAndType(uri, "application/pdf")
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        return intent
    }

    override fun sendInput(input: String) {
        when (input) {
            "Download" -> downloadPDF()
            else -> sharePDF()
        }
    }
}