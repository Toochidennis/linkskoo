package com.digitaldream.winskool.models

class StudentPaymentModel {
    private var mId: Int = 0
    private var mStatus: String? = null
    private var mName: String? = null
    private var mLevelName: String? = null
    private var mClassName: String? = null
    private var mRegistrationNo: String? = null
    private var mTerm: String? = null
    private var mDate: String? = null
    private var mSession: String? = null
    private var mAmount: String? = null
    private var mReferenceNumber: String? = null

    fun getId() = mId

    fun getStatus() = mStatus

    fun getName() = mName

    fun getLevelName() = mLevelName

    fun getClassName() = mClassName

    fun getRegistrationNo() = mRegistrationNo

    fun getTerm() = mTerm

    fun getDate() = mDate

    fun getSession() = mSession

    fun getAmount() = mAmount

    fun getReferenceNumber() = mReferenceNumber

    fun setId(sId: Int) {
        mId = sId
    }

    fun setStatus(sStatus: String) {
        mStatus = sStatus
    }

    fun setName(sName: String) {
        mName = sName
    }

    fun setLevelName(sLevelName: String) {
        mLevelName = sLevelName
    }

    fun setClassName(sClassName: String) {
        mClassName = sClassName
    }

    fun setRegistrationNo(sRegistrationNo: String) {
        mRegistrationNo = sRegistrationNo
    }

    fun setTerm(sTerm: String) {
        mTerm = sTerm
    }

    fun setDate(sDate: String) {
        mDate = sDate
    }

    fun setSession(sSession: String) {
        mSession = sSession
    }

    fun setAmount(sAmount: String) {
        mAmount = sAmount
    }

    fun setReferenceNumber(sReferenceNumber: String) {
        mReferenceNumber = sReferenceNumber
    }

}