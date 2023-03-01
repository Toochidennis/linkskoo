package com.digitaldream.winskool.models

class AdminPaymentDashboardModel {
    private var mId: Int = 0
    private var mInvoice: String? = null
    private var mTransactionName: String? = null
    private var mLevelName: String? = null
    private var mClassName: String? = null
    private var mTransactionTerm: String? = null
    private var mTransactionDate: String? = null
    private var mTransactionSession: String? = null
    private var mExpenditureAmount: String? = null
    private var mReceivedAmount: String? = null
    private var mReferenceNumber: String? = null
    private var mDescription: String? = null

    fun getId() = mId

    fun getInvoice() = mInvoice

    fun getTransactionName() = mTransactionName

    fun getLevelName() = mLevelName

    fun getClassName() = mClassName

    fun getTransactionTerm() = mTransactionTerm

    fun getTransactionDate() = mTransactionDate

    fun getTransactionSession() = mTransactionSession

    fun getExpenditureAmount() = mExpenditureAmount

    fun getReceivedAmount() = mReceivedAmount

    fun getReferenceNumber() = mReferenceNumber

    fun getDescription() = mDescription

    fun setId(sId: Int) {
        mId = sId
    }

    fun setInvoice(sInvoice: String) {
        mInvoice = sInvoice
    }

    fun setTransactionName(sTransactionName: String) {
        mTransactionName = sTransactionName
    }

    fun setLevelName(sLevelName: String) {
        mLevelName = sLevelName
    }

    fun setClassName(sClassName: String) {
        mClassName = sClassName
    }

    fun setTransactionTerm(sTransactionTerm: String) {
        mTransactionTerm = sTransactionTerm
    }

    fun setTransactionDate(sTransactionDate: String) {
        mTransactionDate = sTransactionDate
    }

    fun setTransactionSession(sTransactionSession: String) {
        mTransactionSession = sTransactionSession
    }

    fun setExpenditureAmount(sExpenditureAmount: String) {
        mExpenditureAmount = sExpenditureAmount
    }

    fun setReceivedAmount(sReceivedAmount: String) {
        mReceivedAmount = sReceivedAmount
    }

    fun setReferenceNumber(sReferenceNumber: String) {
        mReferenceNumber = sReferenceNumber
    }

    fun setDescription(sDescription: String) {
        mDescription = sDescription
    }

}