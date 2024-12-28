// app/src/main/java/com/pay2share/ui/home/HomeViewModel.kt
package com.pay2share.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pay2share.data.database.UsuarioRepository

class HomeViewModel(private val usuarioRepository: UsuarioRepository) : ViewModel() {

    private val _grupos = MutableLiveData<List<String>>()
    val grupos: LiveData<List<String>> = _grupos

    fun cargarGrupos(usuarioId: Int) {
        val cursor = usuarioRepository.obtenerGruposDeUsuario(usuarioId)
        val listaGrupos = mutableListOf<String>()
        while (cursor.moveToNext()) {
            listaGrupos.add(cursor.getString(cursor.getColumnIndexOrThrow("name")))
        }
        cursor.close()
        _grupos.value = listaGrupos
    }
}