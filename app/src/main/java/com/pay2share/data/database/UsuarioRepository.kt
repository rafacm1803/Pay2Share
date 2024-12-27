package com.pay2share.data.database

import android.database.Cursor
import com.pay2share.database.DatabaseHelper

class UsuarioRepository (private val dbHelper: DatabaseHelper){
    fun crearUsuario(nombre: String, email: String): Long {
        return dbHelper.insertUser(nombre, email)
    }

    fun obtenerUsuarioPorEmail(email: String): Cursor {
        return dbHelper.getUserByEmail(email)
    }

    fun obtenerUsuarioPorId(id: Int): Cursor {
        return dbHelper.getUserById(id)
    }

    fun actualizarUsuario(id: Int, nuevoNombre: String, nuevoEmail: String, nuevaPassword: String): Int {
        return dbHelper.updateUser(id, nuevoNombre, nuevoEmail, nuevaPassword)
    }

    fun eliminarUsuario(id: Int): Int {
        return dbHelper.deleteUser(id)
    }

    fun anyadirUsuarioAGrupo(usuarioId: Int, grupoId: Int): Long {
        return dbHelper.addUserToGroup(usuarioId, grupoId)
    }

    fun eliminarUsuarioDelGrupo(usuarioId: Int, grupoId: Int): Int {
        return dbHelper.removeUserFromGroup(usuarioId, grupoId)
    }

    fun obtenerGruposDeUsuario(usuarioId: Int): Cursor {
        return dbHelper.getGroupsOfUser(usuarioId)
    }

    fun obtenerTodosLosUsuarios(): Cursor {
        return dbHelper.getAllUsers()
    }
}