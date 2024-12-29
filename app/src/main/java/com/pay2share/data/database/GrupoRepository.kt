package com.pay2share.data.database

import android.content.ContentValues
import android.database.Cursor
import com.pay2share.database.DatabaseHelper

class GrupoRepository(private val dbHelper: DatabaseHelper) {

    fun crearGrupo(nombre: String, creadorId: Int): Long {
        return dbHelper.insertGroup(nombre, creadorId)
    }

    fun obtenerTodosLosGrupos(): Cursor {
        return dbHelper.getAllGroups()
    }

    fun obtenerGrupoById(id: Int): Cursor {
        return dbHelper.getGroupById(id)
    }

    fun obtenerParticipantesPorGrupo(groupId: Int): Cursor {
        return dbHelper.getUsersByGroup(groupId)
    }

    fun actualizarGrupo(id: Int, nuevoNombre: String): Int {
        return dbHelper.updateGroup(id, nuevoNombre)
    }

    fun eliminarGrupo(id: Int): Int {
        return dbHelper.deleteGroup(id)
    }

    fun obtenerTotalDeudaPorGrupo(groupId: Int): Double {
        return dbHelper.getTotalDebtForGroup(groupId)
    }

    fun asignarDeudaAGrupo(userId: Int, groupId: Int, debtAmount: Double) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("debt", debtAmount)
        }
        db.update(DatabaseHelper.TABLE_USER_GROUPS, values, "user_id = ? AND group_id = ?", arrayOf(userId.toString(), groupId.toString()))
    }

    fun obtenerDeudaPorUsuarioYGrupo(userId: Int, groupId: Int): Double {
        return dbHelper.obtenerDeudaPorUsuarioYGrupo(userId, groupId)
    }

    fun obtenerParticipantesConDeudaPositiva(groupId: Int): Cursor {
        return dbHelper.getUsersWithPositiveDebt(groupId)
    }

    fun obtenerParticipantesConDeudaNegativa(groupId: Int): Cursor {
        return dbHelper.getUsersWithNegativeDebt(groupId)
    }

    fun actualizarDeuda(userId: Int, groupId:Int, deuda: Double){
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("debt", deuda)
        }        
        db.update(DatabaseHelper.TABLE_USER_GROUPS, values, "user_id = ? AND group_id = ?", arrayOf(userId.toString(), groupId.toString()))

    }


}
