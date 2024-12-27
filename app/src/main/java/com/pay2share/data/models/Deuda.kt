package com.pay2share.data.models

data class Debt(
    val id: Int,
    val creditor: String,
    val debtor: String,
    val amount: Double
)
