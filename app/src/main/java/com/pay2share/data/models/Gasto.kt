package com.pay2share.data.models

data class Expense(
    val id: Int,
    val name: String,
    val amount: Double,
    val date: String,
    val payer: String,
    val groupId: Int
)
