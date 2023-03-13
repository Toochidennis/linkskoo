package com.digitaldream.winskool.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.digitaldream.winskool.R

class VendorDialog(mContext: Context) : Dialog(mContext) {

    private lateinit var mVendorName: EditText
    private lateinit var mVendorEmail: EditText
    private lateinit var mVendorReference: EditText
    private lateinit var mVendorPhone: EditText
    private lateinit var mVendorAddress: EditText
    private lateinit var mAddBtn: Button
    private lateinit var mBackBtn: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.attributes.windowAnimations = R.style.DialogAnimation
        window!!.setGravity(Gravity.BOTTOM)
        setContentView(R.layout.dialog_vendor)

        mVendorName = findViewById(R.id.vendor_name_input)
        mVendorEmail = findViewById(R.id.vendor_email_input)
        mVendorReference = findViewById(R.id.vendor_reference_input)
        mVendorPhone = findViewById(R.id.vendor_phone_input)
        mVendorAddress = findViewById(R.id.vendor_address_input)
        mAddBtn = findViewById(R.id.add_vendor_btn)
        mBackBtn = findViewById(R.id.back_btn)

        mBackBtn.setOnClickListener {
            dismiss()
        }

        mAddBtn.setOnClickListener {
            validateInput()
        }
    }

    private fun validateInput() {
        val regex = Regex("^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]\$")
        val name = mVendorName.text.toString().trim()
        val email = mVendorEmail.text.toString().trim()
        val reference = mVendorReference.text.toString().trim()
        val phone = mVendorPhone.text.toString().trim()
        val address = mVendorAddress.text.toString().trim()

        if (name.isEmpty()) {
            mVendorName.error = "Name is empty"
        } else if (reference.isEmpty()) {
            mVendorReference.error = "Reference is empty"
        } else if (phone.isEmpty()) {
            mVendorPhone.error = "Phone is empty"
        } else if (email.isNotEmpty() && !email.matches(regex)) {
            mVendorEmail.error = "Invalid email"
        } else {
            println("name: $name email: $email ref: $reference phone: $phone address: $address")
            dismiss()
        }

    }
}