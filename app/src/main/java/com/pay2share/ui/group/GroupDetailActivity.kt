package com.pay2share.ui.group

import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pay2share.R
import com.pay2share.ui.group.Participant
import com.pay2share.databinding.ActivityGroupDetailBinding
import com.pay2share.database.DatabaseHelper
import com.pay2share.data.database.GrupoRepository
import com.pay2share.data.database.UsuarioRepository
import com.pay2share.data.database.GastoRepository
import com.pay2share.data.database.DeudaRepository
import com.pay2share.ui.participant.ParticipantDetailActivity

class GroupDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroupDetailBinding
    private lateinit var grupoRepository: GrupoRepository
    private lateinit var usuarioRepository: UsuarioRepository
    private lateinit var gastoRepository: GastoRepository
    private lateinit var deudaRepository: DeudaRepository
    private var isCreator: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dbHelper = DatabaseHelper(this)
        grupoRepository = GrupoRepository(dbHelper)
        usuarioRepository = UsuarioRepository(dbHelper)
        gastoRepository = GastoRepository(dbHelper)
        deudaRepository = DeudaRepository(dbHelper)

        val groupId = intent.getIntExtra("GROUP_ID", -1)
        val sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", -1)

        if (groupId != -1) {
            val groupCursor = grupoRepository.obtenerGrupoById(groupId)
            if (groupCursor.moveToFirst()) {
                val groupName = groupCursor.getString(groupCursor.getColumnIndexOrThrow("name"))
                val creatorId = groupCursor.getInt(groupCursor.getColumnIndexOrThrow("creator_id"))
                binding.textGroupName.text = groupName
                isCreator = (userId == creatorId)
            }
            groupCursor.close()

            val participantsCursor = grupoRepository.obtenerParticipantesPorGrupo(groupId)
            val participants = mutableListOf<Participant>()
            while (participantsCursor.moveToNext()) {
                val participantName = participantsCursor.getString(participantsCursor.getColumnIndexOrThrow("name"))
                val participantId = participantsCursor.getInt(participantsCursor.getColumnIndexOrThrow("id"))
                val debt = grupoRepository.obtenerDeudaPorUsuarioYGrupo(participantId, groupId)
                participants.add(Participant(participantName, debt))
            }
            participantsCursor.close()

            val totalDebt = grupoRepository.obtenerTotalDeudaPorGrupo(groupId)
            binding.textTotalDebt.text = totalDebt.toString()

            val listViewParticipants: ListView = findViewById(R.id.listViewParticipants)
            val adapter = ParticipantAdapter(this, participants)
            listViewParticipants.adapter = adapter

            listViewParticipants.setOnItemClickListener { _, _, position, _ ->
                val selectedParticipant = participants[position] // Obtén el participante seleccionado
                val intent = Intent(this, ParticipantDetailActivity::class.java)
                val partId = usuarioRepository.obtenerIdUsuarioPorNombre(selectedParticipant.name)
                intent.putExtra("PARTICIPANT_ID", partId) // Pasa el ID del participante
                intent.putExtra("GROUP_ID", groupId) // Opcional, si necesitas datos del grupo
                startActivity(intent)
            }
        }

        binding.buttonAddUser.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            if (email.isEmpty()) {
                binding.editTextEmail.error = "Email is required"
                return@setOnClickListener
            }
            val userCursor = usuarioRepository.obtenerUsuarioPorEmail(email)
            if (userCursor != null && userCursor.moveToFirst()) {
                val userId = userCursor.getInt(userCursor.getColumnIndexOrThrow("id"))
                usuarioRepository.anyadirUsuarioAGrupo(userId, groupId)
                Toast.makeText(this, "User added to group", Toast.LENGTH_SHORT).show()
                userCursor.close()

                val participantsCursor = grupoRepository.obtenerParticipantesPorGrupo(groupId)
                val participants = mutableListOf<Participant>()
                while (participantsCursor.moveToNext()) {
                    val participantName = participantsCursor.getString(participantsCursor.getColumnIndexOrThrow("name"))
                    val participantId = participantsCursor.getInt(participantsCursor.getColumnIndexOrThrow("id"))
                    val debt = grupoRepository.obtenerDeudaPorUsuarioYGrupo(participantId, groupId)
                    participants.add(Participant(participantName, debt))
                }
                participantsCursor.close()

                binding.textTotalDebt.text = grupoRepository.obtenerTotalDeudaPorGrupo(groupId).toString()
                val listViewParticipants: ListView = findViewById(R.id.listViewParticipants)
                val adapter = ParticipantAdapter(this, participants)
                listViewParticipants.adapter = adapter
                adapter.notifyDataSetChanged()

            } else {
                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonAddExpense.setOnClickListener {
            val expenseName = binding.editTextExpenseName.text.toString()
            val expenseAmount = binding.editTextExpenseAmount.text.toString().toDoubleOrNull()
            if (expenseName.isEmpty()) {
                binding.editTextExpenseName.error = "Expense name is required"
                return@setOnClickListener
            }
            if (expenseAmount == null) {
                binding.editTextExpenseAmount.error = "Valid expense amount is required"
                return@setOnClickListener
            }
            val date = System.currentTimeMillis().toString() // You can format this as needed
            gastoRepository.crearGasto(expenseName, expenseAmount, date, userId.toString(), groupId)
            //Añadirle deuda positiva al usuario que ha añadido el gasto y deuda negativa a los demás del grupo
            val participantsCursor = grupoRepository.obtenerParticipantesPorGrupo(groupId)
            while (participantsCursor.moveToNext()) {
                val participantId = participantsCursor.getInt(participantsCursor.getColumnIndexOrThrow("id"))
                val nombreCreador = usuarioRepository.obtenerNombreUsuarioPorId(userId) ?: "N/A"
                val debt = grupoRepository.obtenerDeudaPorUsuarioYGrupo(participantId, groupId)
                if (participantId == userId) {
                    deudaRepository.aumentarDeuda(userId, groupId, debt + (expenseAmount / participantsCursor.count) * (participantsCursor.count - 1) )
                } else {
                    val nombreDeudor = usuarioRepository.obtenerNombreUsuarioPorId(participantId) ?: "N/A"
                    deudaRepository.crearDeuda(nombreCreador, nombreDeudor, debt - expenseAmount / participantsCursor.count)
                    deudaRepository.aumentarDeuda(participantId, groupId, debt - expenseAmount / participantsCursor.count)
                }
            }
            Toast.makeText(this, "Expense added to group", Toast.LENGTH_SHORT).show()
            recreate()
        }
    }
}