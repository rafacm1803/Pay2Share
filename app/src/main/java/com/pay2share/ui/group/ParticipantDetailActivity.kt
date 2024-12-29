package com.pay2share.ui.participant

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.pay2share.R
import com.pay2share.databinding.ActivityGroupDetailBinding
import com.pay2share.database.DatabaseHelper
import com.pay2share.data.database.GrupoRepository
import com.pay2share.data.database.UsuarioRepository
import com.pay2share.data.database.GastoRepository
import com.pay2share.data.database.DeudaRepository

class ParticipantDetailActivity : AppCompatActivity() {

    private lateinit var usuarioRepository: UsuarioRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_participant_detail)

        // Inicializa repositorio
        val dbHelper = DatabaseHelper(this)
        usuarioRepository = UsuarioRepository(dbHelper)

        // Obtén el ID del participante del Intent
        val participantId = intent.getIntExtra("PARTICIPANT_ID", -1)
        val groupId = intent.getIntExtra("GROUP_ID", -1)
        val gastoRepository = GastoRepository(dbHelper)

        if (participantId != -1) {
            val participantCursor = usuarioRepository.obtenerUsuarioPorId(participantId)
            if (participantCursor != null && participantCursor.moveToFirst()) {
                val name = participantCursor.getString(participantCursor.getColumnIndexOrThrow("name"))
                val email = participantCursor.getString(participantCursor.getColumnIndexOrThrow("email"))

                // Muestra los datos en el layout
                findViewById<TextView>(R.id.textParticipantName).text = name

                participantCursor.close()
            }
        }

        if (groupId != -1 && participantId != -1) {
            val expensesCursor = gastoRepository.obtenerGastosPorGrupo(groupId)
            val userExpenses = mutableListOf<String>()
            while (expensesCursor.moveToNext()) {
                val payer = expensesCursor.getString(expensesCursor.getColumnIndexOrThrow("payer"))
                if (payer == participantId.toString()) {
                    val expenseName = expensesCursor.getString(expensesCursor.getColumnIndexOrThrow("name"))
                    val expenseAmount = expensesCursor.getDouble(expensesCursor.getColumnIndexOrThrow("amount"))
                    userExpenses.add("$expenseName: $$expenseAmount")
                }
            }
            expensesCursor.close()

            // Muestra los gastos en un TextView (debes tener un TextView con este id en tu layout)
            findViewById<TextView>(R.id.textViewUserExpenses).text = userExpenses.joinToString("\n")
        }
    }
}