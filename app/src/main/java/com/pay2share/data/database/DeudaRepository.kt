package com.pay2share.data.database

import android.database.Cursor
import com.pay2share.database.DatabaseHelper

class DeudaRepository (private val dbHelper: DatabaseHelper){

    fun crearDeuda(creditor: String, debtor: String, amount: Double): Long {
        return dbHelper.insertDebt(creditor, debtor, amount)
    }

    fun obtenerDeudasPorCredor(creditor: String): Cursor {
        return dbHelper.getDebtsByCreditor(creditor)
    }

    fun obtenerDeudasPorDeudor(debtor: String): Cursor {
        return dbHelper.getDebtsByDebtor(debtor)
    }

    fun actualizarDeuda(id: Int, nuevoCredor: String, nuevoDeudor: String, nuevoMonto: Double): Int {
        return dbHelper.updateDebt(id, nuevoCredor, nuevoDeudor, nuevoMonto)
    }

    fun eliminarDeuda(id: Int): Int {
        return dbHelper.deleteDebt(id)
    }

    fun obtenerDeudaPorId(id: Int): Cursor {
        return dbHelper.getDebtById(id)
    }
    fun obtenerTotalDeudaPorCredor(creditor: String): Double {
        return dbHelper.getTotalDebtForPerson(creditor)
    }

    fun obtenerTotalDeudaPorDeudor(debtor: String): Double {
        return dbHelper.getTotalCreditForPerson(debtor)
    }

    fun obtenerTotalDeudaPorGrupo(groupId: Int): Double {
        return dbHelper.getTotalDebtForGroup(groupId)
    }

    fun aumentarDeuda(userId: Int, groupId: Int, amount: Double): Int {
        return dbHelper.increaseDebt(userId, groupId, amount)
    }
}