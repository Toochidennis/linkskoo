package com.digitaldream.winskool.models

class AdminPaymentModel {

    private var mId: Int = 0
    private var mInvoice: String? = null
    private var mStudentId: String? = null
    private var mTransactionName: String? = null
    private var mStudentName: String? = null
    private var mLevelName: String? = null
    private var mClassName: String? = null
    private var mTransactionDate: String? = null
    private var mReceivedAmount: String? = null
    private var mReferenceNumber: String? = null
    private var mRegistrationNumber: String? = null
    private var mDescription: String? = null
    private var mTerm: String? = null
    private var mSession: String? = null
    private var mJson: String? = null

    fun getId() = mId
    fun getStudentId() = mStudentId

    fun getInvoice() = mInvoice

    fun getTransactionName() = mTransactionName
    fun getStudentName() = mStudentName

    fun getLevelName() = mLevelName

    fun getClassName() = mClassName

    fun getReceivedAmount() = mReceivedAmount

    fun getReferenceNumber() = mReferenceNumber
    fun getRegistrationNumber() = mRegistrationNumber

    fun getDescription() = mDescription

    fun getTerm() = mTerm
    fun getSession() = mSession

    fun getJson() = mJson
    fun getTransactionDate() = mTransactionDate


    fun setId(sId: Int) {
        mId = sId
    }

    fun setStudentId(sStudentId: String){
        mStudentId = sStudentId
    }

    fun setInvoice(sInvoice: String) {
        mInvoice = sInvoice
    }

    fun setTransactionName(sTransactionName: String) {
        mTransactionName = sTransactionName
    }

    fun setStudentName(sStudentName: String) {
        mStudentName = sStudentName
    }


    fun setLevelName(sLevelName: String) {
        mLevelName = sLevelName
    }

    fun setClassName(sClassName: String) {
        mClassName = sClassName
    }


    fun setTransactionDate(sTransactionDate: String) {
        mTransactionDate = sTransactionDate
    }


    fun setReceivedAmount(sReceivedAmount: String) {
        mReceivedAmount = sReceivedAmount
    }

    fun setReferenceNumber(sReferenceNumber: String) {
        mReferenceNumber = sReferenceNumber
    }

    fun setRegistrationNumber(sRegistrationNumber: String) {
        mRegistrationNumber = sRegistrationNumber
    }

    fun setDescription(sDescription: String) {
        mDescription = sDescription
    }

    fun setTerm(sTerm: String) {
        mTerm = sTerm
    }

    fun setSession(sSession: String) {
        mSession = sSession
    }

    fun setJson(sJson: String) {
        mJson = sJson
    }

    fun setDate(sTransactionDate: String){
        mTransactionDate = sTransactionDate
    }

}