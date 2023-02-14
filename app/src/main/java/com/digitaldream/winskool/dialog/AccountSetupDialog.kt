package com.digitaldream.winskool.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.widget.*
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.digitaldream.winskool.R
import com.digitaldream.winskool.activities.Login
import org.json.JSONException
import org.json.JSONObject

class AccountSetupDialog(
    sContext: Context,
    private val sFrom: String,
    private val sId: String,
    private val sName: String,
    private val sAccountId: String,
    private val sAccountType: String
) : Dialog(sContext) {

    private lateinit var mAccountName: EditText
    private lateinit var mAccountId: EditText
    private lateinit var mAccountType: Spinner
    private lateinit var mBackBtn: ImageView
    private lateinit var mDoneBtn: Button

    private var mSelectedAccount: String? = null
    private var mDb: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.attributes.windowAnimations = R.style.DialogAnimation
        window!!.setGravity(Gravity.BOTTOM)

        setContentView(R.layout.dialog_setup_account)

        mAccountName = findViewById(R.id.account_name)
        mAccountId = findViewById(R.id.account_id)
        mAccountType = findViewById(R.id.spinner_account_type)
        mBackBtn = findViewById(R.id.back_btn)
        mDoneBtn = findViewById(R.id.add_account_btn)

        val sharedPreferences = context.getSharedPreferences(
            "loginDetail",
            Context.MODE_PRIVATE
        )
        mDb = sharedPreferences.getString("db", "")

        val accountTypes = context.resources.getStringArray(R.array.account_type)
        val spinnerAdapter = ArrayAdapter(
            context, android.R.layout.simple_spinner_dropdown_item, accountTypes
        )
        mAccountType.adapter = spinnerAdapter

        mAccountType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                mSelectedAccount = accountTypes[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        mBackBtn.setOnClickListener {
            dismiss()
        }

        mDoneBtn.setOnClickListener {
            setAccount()
            dismiss()
        }

    }

    private fun setAccount() {

        println("Name: ${mAccountName.text}")
        println("Id: ${mAccountId.text}")
        println("Type: $mSelectedAccount")

        val url = Login.urlBase + "/manageAccount.php"
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST, url,
            { response: String ->
                Log.d("set account", response)
                try {
                    val jsonObject = JSONObject(response)
                    val status = jsonObject.getString("status")

                    if (status == "success") {

                        if (sFrom == "edit")
                            Toast.makeText(
                                context, "Account edited Successfully", Toast
                                    .LENGTH_SHORT
                            )
                                .show()
                        else
                            Toast.makeText(
                                context, "Account added Successfully", Toast
                                    .LENGTH_SHORT
                            )
                                .show()
                    }
                } catch (ex: JSONException) {
                    ex.printStackTrace()
                }


            }, { error: VolleyError ->
                error.printStackTrace()
                Toast.makeText(
                    context, error.message,
                    Toast.LENGTH_SHORT
                ).show()
            }) {
            override fun getParams(): Map<String, String> {
                val stringMap: MutableMap<String, String> = HashMap()
                stringMap["account_name"] = mAccountName.text.toString()
                stringMap["account_id"] = mAccountId.text.toString()
                when (mSelectedAccount) {
                    "Income" -> stringMap["account_type"] = "0"
                    "Expenditure" -> stringMap["account_type"] = "1"
                }
                stringMap["_db"] = mDb!!
                return stringMap
            }
        }
        val requestQueue: RequestQueue = Volley.newRequestQueue(context)
        requestQueue.add(stringRequest)
    }

}