package com.digitaldream.winskool.models

data class TimeFrameDataModel(val getData: () -> Unit) {

    var startDate: String? = null
    var endDate: String? = null
    var grouping: String? = null
    var duration: String? = null
    var levelName: String? = null
    var levelData: String? = null
    var classData: String? = null
    var className: String? = null
    var levelId: String? = null
    var classId: String? = null
    var vendor: String? = null
    var account: String? = null

}