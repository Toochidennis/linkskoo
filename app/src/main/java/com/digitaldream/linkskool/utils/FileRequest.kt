package com.digitaldream.linkskool.utils

import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.HttpHeaderParser
import java.io.File
import java.io.FileOutputStream

class FileRequest(
    method: Int,
    url: String,
    private val filePath: String,
    private val successListener: (ByteArray) -> Unit,
    errorListener: (Exception) -> Unit
) : Request<ByteArray>(method, url, Response.ErrorListener { error -> errorListener(error) }) {


    override fun parseNetworkResponse(response: NetworkResponse?): Response<ByteArray> {
        return try {
            saveToFile(response!!.data)
            Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response))
        } catch (e: VolleyError) {
            Response.error(e)
        }
    }

    override fun deliverResponse(response: ByteArray?) {
        successListener(response!!)
    }

    private fun saveToFile(data: ByteArray) {
        val file = File(filePath)
        val outputStream = FileOutputStream(file)
        outputStream.write(data)
        outputStream.close()
    }
}