package com.digitaldream.winskool.models

data class TimeFrameData(val getData: () -> Unit) {
    var startDate: String? = null
    var endDate: String? = null
    var others: String? = null
}