package com.digitaldream.ddl.models

class TermFeesDataModel {

    private var mFeedId: Int = 0
    private var mFeeName: String? = null
    private var mFeeAmount: String? = null
    private var mMandatory: String? = null
    private var mTerm: String? = null
    private var mYear: String? = null
    private var mDate: String? = null
    private var mAmount: String? = null
    private var mInvoiceId: String? = null


    fun getFeeId() = mFeedId

    fun getFeeName() = mFeeName

    fun getFeeAmount() = mFeeAmount
    fun getMandatory() = mMandatory

    fun getInvoiceId() = mInvoiceId

    fun getTerm() = mTerm
    fun getYear() = mYear

    fun getDate() = mDate


    fun getAmount() = mAmount

    fun setFeeName(sFeeName: String) {
        mFeeName = sFeeName
    }

    fun setFeeAmount(sFeeAmount: String) {
        mFeeAmount = sFeeAmount
    }


    fun setFeeId(sFeeId: Int) {
        mFeedId = sFeeId
    }

    fun setMandatory(sMandatory: String) {
        mMandatory = sMandatory
    }

    fun setInvoiceId(sInvoiceId: String) {
        mInvoiceId = sInvoiceId
    }

    fun setTerm(sTerm: String) {
        mTerm = sTerm
    }

    fun setYear(sYear: String) {
        mYear = sYear
    }

    fun setDate(sDate: String) {
        mDate = sDate
    }

    fun setAmount(sAmount: String) {
        mAmount = sAmount
    }
}