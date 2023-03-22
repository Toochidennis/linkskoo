package com.digitaldream.ddl.models

class AccountSetupDataModel() {

    private var mId: Int = 0
    private var mAccountName: String? = null
    private var mAccountId: String? = null
    private var mAccountType: String? = null

    fun setId(sId: Int) {
        mId = sId
    }

    fun getId() = mId

    fun setAccountName(sAccountName: String) {
        mAccountName = sAccountName
    }

    fun getAccountName() = mAccountName

    fun setAccountId(sAccountId: String) {
        mAccountId = sAccountId
    }

    fun getAccountId() = mAccountId

    fun setAccountType(sAccountType: String) {
        mAccountType = sAccountType
    }

    fun getAccountType() = mAccountType

}