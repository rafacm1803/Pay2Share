package com.pay2share.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pay2share.data.database.UsuarioRepository
import com.pay2share.data.models.Group

class HomeViewModel(private val usuarioRepository: UsuarioRepository) : ViewModel() {

    private val _grupos = MutableLiveData<List<Group>>()
    val grupos: LiveData<List<Group>> = _grupos

    fun cargarGrupos(usuarioId: Int) {
        val cursor = usuarioRepository.obtenerGruposDeUsuario(usuarioId)
        val listaGrupos = mutableListOf<Group>()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            listaGrupos.add(Group(id, name))
        }
        cursor.close()
        _grupos.value = listaGrupos
    }
}