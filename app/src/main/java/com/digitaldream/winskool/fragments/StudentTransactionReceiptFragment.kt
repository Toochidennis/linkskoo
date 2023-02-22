package com.digitaldream.winskool.fragments

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
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
import com.digitaldream.winskool.utils.CHANNEL_ID
import com.digitaldream.winskool.utils.UtilsFun
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

private const val ARG_AMOUNT = "amount"
private const val ARG_REFERENCE = "reference"
private const val ARG_STATUS = "status"
private const val ARG_SESSION = "session"
private const val ARG_TERM = "term"
private const val ARG_DATE = "date"

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class StudentTransactionReceiptFragment : Fragment() {

    private var mAmount: String? = null
    private var mReference: String? = null
    private var mStatus: String? = null
    private var mSession: String? = null
    private var mTerm: String? = null
    private var mDate: String? = null

    private lateinit var notificationManger: NotificationManagerCompat
    private lateinit var mReceiptCard: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mAmount = it.getString(ARG_AMOUNT)
            mReference = it.getString(ARG_REFERENCE)
            mStatus = it.getString(ARG_STATUS)
            mSession = it.getString(ARG_SESSION)
            mTerm = it.getString(ARG_TERM)
            mDate = it.getString(ARG_DATE)
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
        fun newInstance(
            amount: String,
            reference: String,
            status: String,
            session: String,
            term: String,
            date: String,
        ) =
            StudentTransactionReceiptFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_AMOUNT, amount)
                    putString(ARG_REFERENCE, reference)
                    putString(ARG_STATUS, status)
                    putString(ARG_SESSION, session)
                    putString(ARG_TERM, term)
                    putString(ARG_DATE, date)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(
            R.layout.fragment_receipt_transaction_student,
            container, false
        )

        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        mReceiptCard = view.findViewById(R.id.receipt_card)
        val shareBtn: Button = view.findViewById(R.id.share_receipt)
        val downloadBtn: Button = view.findViewById(R.id.download_receipt)

        toolbar.apply {
            setNavigationIcon(R.drawable.arrow_left)
            title = "Payment Receipt"
            setNavigationOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        }

        downloadBtn.setOnClickListener {
            downloadPDF()
        }

        shareBtn.setOnClickListener {
            sharePDF()
        }

        generateReceipt(view)

        return view
    }

    private fun generateReceipt(view: View) {

        val schoolName: TextView = view.findViewById(R.id.school_name)
        val amount: TextView = view.findViewById(R.id.paid_amount)
        val status: TextView = view.findViewById(R.id.status)
        val date: TextView = view.findViewById(R.id.date)
        val name: TextView = view.findViewById(R.id.student_name)
        val level: TextView = view.findViewById(R.id.student_level)
        val studentClass: TextView = view.findViewById(R.id.student_class)
        val studentRegNo: TextView = view.findViewById(R.id.registration_no)
        val session: TextView = view.findViewById(R.id.session)
        val term: TextView = view.findViewById(R.id.term)
        val referenceNumber: TextView = view.findViewById(R.id.reference_number)

        val sharedPreferences = requireContext().getSharedPreferences(
            "loginDetail", Context
                .MODE_PRIVATE
        )
        val mSchoolName = sharedPreferences.getString("school_name", "")
        val mClass = sharedPreferences.getString("student_class", "")
        val studentName = sharedPreferences.getString("user", "")
        val mRegNo = sharedPreferences.getString("student_reg_no", "")

        schoolName.text = mSchoolName
        amount.text = mAmount
        status.text = mStatus
        date.text = mDate
        name.text = UtilsFun.capitaliseFirstLetter(studentName!!)
        level.text = ""
        studentClass.text = mClass
        studentRegNo.text = mRegNo
        session.text = mSession
        term.text = mTerm
        referenceNumber.text = mReference
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
                requireContext(), CHANNEL_ID
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

}