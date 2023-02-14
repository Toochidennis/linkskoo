package com.digitaldream.winskool.models

class TermFeesDataModel {

    private var mFeedId: Int = 0
    private var mFeeName: String? = null
    private var mFeeAmount: String? = null
    private var mMandatory: String? = null

    fun setFeeId(sFeeId: Int) {
        mFeedId = sFeeId
    }

    fun getFeeId() = mFeedId

    fun setFeeName(sFeeName: String) {
        mFeeName = sFeeName
    }

    fun getFeeName() = mFeeName

    fun setFeeAmount(sFeeAmount: String) {
        mFeeAmount = sFeeAmount
    }

    fun getFeeAmount() = mFeeAmount

    fun setMandatory(sMandatory: String) {
        mMandatory = sMandatory
    }

    fun getMandatory() = mMandatory
}