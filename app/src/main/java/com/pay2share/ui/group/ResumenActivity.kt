package com.pay2share.ui.group

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.pay2share.R
import com.pay2share.database.DatabaseHelper
import com.pay2share.data.database.GrupoRepository
import com.pay2share.data.database.UsuarioRepository
import com.pay2share.data.database.GastoRepository
import com.pay2share.data.database.DeudaRepository
import java.util.Locale

class ResumenActivity : AppCompatActivity() {

    private lateinit var grupoRepository: GrupoRepository
    private lateinit var usuarioRepository: UsuarioRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resumen)

        val dbHelper = DatabaseHelper(this) // Crea el helper de la base de datos
        grupoRepository = GrupoRepository(dbHelper) // Inicializa grupoRepository
        usuarioRepository = UsuarioRepository(dbHelper) // Inicializa usuarioRepository

        val textViewPagos = findViewById<TextView>(R.id.textViewPagos) // TextView para mostrar los pagos
        val currentLanguage = Locale.getDefault().language
    
        val groupId = intent.getIntExtra("GROUP_ID", -1)
    
        val positivoCursor = grupoRepository.obtenerParticipantesConDeudaPositiva(groupId)
        val negativoCursor = grupoRepository.obtenerParticipantesConDeudaNegativa(groupId)
    
        val pagosList = mutableListOf<String>() // Lista para almacenar los mensajes de pagos

        if (positivoCursor.moveToFirst() && negativoCursor.moveToFirst()) {
            do {
                var deudaPositiva = positivoCursor.getDouble(positivoCursor.getColumnIndexOrThrow("debt"))
                var deudaNegativa = negativoCursor.getDouble(negativoCursor.getColumnIndexOrThrow("debt"))

                val userIdPositivo = positivoCursor.getInt(positivoCursor.getColumnIndexOrThrow("user_id"))
                val userIdNegativo = negativoCursor.getInt(negativoCursor.getColumnIndexOrThrow("user_id"))

                var userNamePositivo = usuarioRepository.obtenerNombreUsuarioPorId(userIdPositivo) // Obtener nombre del usuario positivo
                var userNameNegativo = usuarioRepository.obtenerNombreUsuarioPorId(userIdNegativo) // Obtener nombre del usuario negativo

                while (deudaPositiva > 0 && deudaNegativa < 0) {
                    val ajuste = minOf(deudaPositiva, -deudaNegativa)


                    Log.d("DEBUG", "1 deudaP es: $deudaPositiva y deudaN es: $deudaNegativa")

                    // Ajustar las deudas
                    deudaPositiva -= ajuste
                    deudaNegativa += ajuste

                    Log.d("DEBUG", "2 deudaP es: $deudaPositiva y deudaN es: $deudaNegativa")

                    // Guardar el mensaje en la lista
                    if (currentLanguage == "es") {
                        pagosList.add(getString(R.string.payment_message, userNameNegativo, ajuste, userNamePositivo))
                    } else {
                        pagosList.add(getString(R.string.payment_message, userNameNegativo, ajuste, userNamePositivo))
                    }



                    // Verificar si se deben mover los cursores
                    if (deudaPositiva == 0.0) {
                        if (positivoCursor.moveToNext()) {
                            deudaPositiva = positivoCursor.getDouble(positivoCursor.getColumnIndexOrThrow("debt"))
                            // Re-obtenemos el nombre actualizado para el siguiente iteración
                            userNamePositivo = usuarioRepository.obtenerNombreUsuarioPorId(positivoCursor.getInt(positivoCursor.getColumnIndexOrThrow("user_id")))
                        } else {
                            break
                        }
                    }

                    if (deudaNegativa == 0.0) {
                        if (negativoCursor.moveToNext()) {
                            deudaNegativa = negativoCursor.getDouble(negativoCursor.getColumnIndexOrThrow("debt"))
                            // Re-obtenemos el nombre actualizado para el siguiente iteración
                            userNameNegativo = usuarioRepository.obtenerNombreUsuarioPorId(negativoCursor.getInt(negativoCursor.getColumnIndexOrThrow("user_id")))
                        } else {
                            break // No hay más negativos
                        }
                    }
                }
            } while (!positivoCursor.isAfterLast && !negativoCursor.isAfterLast)
        }


        // Mostrar la lista de pagos en el TextView
        textViewPagos.text = pagosList.joinToString("\n")
    }
    

}