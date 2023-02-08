package com.digitaldream.winskool.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.widget.Button
import com.digitaldream.winskool.R

class TermlyFeeDialog(sContext: Context, private var sInputListener: OnInputListener) : Dialog
    (sContext) {

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


        jss1Btn.setOnClickListener {
            sInputListener.sendInput(jss1Btn.text.toString())
            dismiss()
        }

        jss2Btn.setOnClickListener {
            sInputListener.sendInput(jss2Btn.text.toString())
            dismiss()
        }

        jss3Btn.setOnClickListener {
            sInputListener.sendInput(jss3Btn.text.toString())
            dismiss()
        }

        sss1Btn.setOnClickListener {
            sInputListener.sendInput(sss1Btn.text.toString())
            dismiss()
        }

        sss2Btn.setOnClickListener {
            sInputListener.sendInput(sss2Btn.text.toString())
            dismiss()
        }

        sss3Btn.setOnClickListener {
            sInputListener.sendInput(sss3Btn.text.toString())
            dismiss()
        }

    }

    interface OnInputListener {
        fun sendInput(input: String)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        try {
            sInputListener = context as OnInputListener
        } catch (e: java.lang.ClassCastException) {
            println(e.printStackTrace())
        }
    }
}