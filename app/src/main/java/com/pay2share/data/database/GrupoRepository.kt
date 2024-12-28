package com.pay2share.data.database

import android.database.Cursor
import com.pay2share.database.DatabaseHelper

class GrupoRepository(private val dbHelper: DatabaseHelper) {

    fun crearGrupo(nombre: String): Long {
        return dbHelper.insertGroup(nombre)
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
}
