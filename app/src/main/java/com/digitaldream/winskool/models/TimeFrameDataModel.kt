package com.digitaldream.winskool.models

data class TimeFrameDataModel(val getData: () -> Unit) {

    var startDate: String? = null
    var endDate: String? = null
    var grouping: String? = null
    var duration: String? = null
    var levelName: String? = null
    var className: String? = null
    var levelId: String? = null
    var classId: String? = null
    var vendorName: String? = null
    var vendorId: String? = null
    var accountName: String? = null
    var accountId: String? = null
}