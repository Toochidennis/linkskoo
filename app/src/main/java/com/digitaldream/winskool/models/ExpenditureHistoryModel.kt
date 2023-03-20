package com.digitaldream.winskool.models

class ExpenditureHistoryModel {

    private var mId: Int = 0
    private var mVendorName: String? = null
    private var mType: String? = null
    private var mDate: String? = null
    private var mAmount: String? = null
    private var mPhone: String? = null
    private var mReferenceNumber: String? = null
    private var mTerm: String? = null
    private var mSession: String? = null

    fun getId() = mId

    fun getVendorName() = mVendorName

    fun getType() = mType

    fun getDate() = mDate

    fun getAmount() = mAmount
    fun getPhone() = mPhone

    fun getReferenceNumber() = mReferenceNumber

    fun getTerm() = mTerm

    fun getSession() = mSession

    fun setId(sId: Int) {
        mId = sId
    }

    fun setVendorName(sVendorName: String) {
        mVendorName = sVendorName
    }

    fun setTypeName(sType: String) {
        mType = sType
    }

    fun setDate(sDate: String) {
        mDate = sDate
    }

    fun setAmount(sAmount: String) {
        mAmount = sAmount
    }
    fun setPhone(sPhone: String){
        mPhone = sPhone
    }

    fun setReferenceNumber(sReferenceNumber: String) {
        mReferenceNumber = sReferenceNumber
    }

    fun setTerm(sTerm: String) {
        mTerm = sTerm
    }

    fun setSession(sSession: String) {
        mSession = sSession
    }
}