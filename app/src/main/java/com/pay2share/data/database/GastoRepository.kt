package com.pay2share.data.database

import android.database.Cursor
import com.pay2share.database.DatabaseHelper

class GastoRepository (private val dbHelper: DatabaseHelper){
    fun crearGasto(nombre: String, monto: Double, fecha: String, pagadoPor: String, grupoId: Int): Long{
        return dbHelper.insertExpense(nombre, monto, fecha, pagadoPor, grupoId)
    }

    fun obtenerGastosPorGrupo(grupoId: Int): Cursor {
        return dbHelper.getExpensesByGroup(grupoId)
    }

    fun eliminarGasto(id: Int): Int {
        return dbHelper.deleteExpense(id)
    }

    fun obtenerGastoPorId(id: Int): Cursor {
        return dbHelper.getExpenseById(id)
    }
}