package com.digitaldream.winskool.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.digitaldream.winskool.R

class TermlyFeeDialog(
    private val sContext: Context,
    private var sInputListener: OnInputListener,
    private var sCurrentText: String
) : Dialog(sContext) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.attributes.windowAnimations = R.style.DialogAnimation
        window!!.setGravity(Gravity.BOTTOM)
        setContentView(R.layout.dialog_fee_termly)

        val jss1Btn: Button = findViewById(R.id.jss_1)
        val jss2Btn: Button = findViewById(R.id.jss_2)
        val jss3Btn: Button = findViewById(R.id.jss_3)
        val sss1Btn: Button = findViewById(R.id.sss_1)
        val sss2Btn: Button = findViewById(R.id.sss_2)
        val sss3Btn: Button = findViewById(R.id.sss_3)

        buttonOnclickListener(jss1Btn)
        buttonOnclickListener(jss2Btn)
        buttonOnclickListener(jss3Btn)
        buttonOnclickListener(sss1Btn)
        buttonOnclickListener(sss2Btn)
        buttonOnclickListener(sss3Btn)

        when (sCurrentText) {
            "JSS 1" -> {
                jss1Btn.apply {
                    //setBackgroundResource(R.drawable.ripple_effect2)
                    setTextColor(ContextCompat.getColor(sContext, R.color.white))
                }
            }
            "JSS 2" -> {
                jss2Btn.apply {
                    setBackgroundResource(R.drawable.ripple_effect2)
                    setTextColor(ContextCompat.getColor(sContext, R.color.white))
                }
            }
            "JSS 3" -> {
                jss3Btn.apply {
                    setBackgroundResource(R.drawable.ripple_effect2)
                    setTextColor(ContextCompat.getColor(sContext, R.color.white))
                }
            }
            "SSS 1" -> {
                sss1Btn.apply {
                    setBackgroundResource(R.drawable.ripple_effect2)
                    setTextColor(ContextCompat.getColor(sContext, R.color.white))
                }
            }
            "SSS 2" -> {
                sss2Btn.apply {
                    setBackgroundResource(R.drawable.ripple_effect2)
                    setTextColor(ContextCompat.getColor(sContext, R.color.white))
                }
            }
            else -> {
                sss3Btn.apply {
                    setBackgroundResource(R.drawable.ripple_effect2)
                    setTextColor(ContextCompat.getColor(sContext, R.color.white))
                }
            }
        }
    }

    private fun buttonOnclickListener(sButton: Button) {
        sButton.setOnClickListener {
            AlertDialog.Builder(sContext).apply {
                setTitle("Warning!")
                setMessage("Your unsaved changes will be lost if you change level")
                setCancelable(false)
                setPositiveButton("I have saved my changes") { _, _ ->
                    sInputListener.sendInput(sButton.text.toString())
                    dismiss()
                }
                setNegativeButton("No") { _, _ -> dismiss() }
                show()
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        try {
            sInputListener = context as OnInputListener
        } catch (e: ClassCastException) {
            println(e.printStackTrace())
        }
    }
}

interface OnInputListener {
    fun sendInput(input: String)

}