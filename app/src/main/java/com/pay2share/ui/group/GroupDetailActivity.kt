package com.pay2share.ui.group

import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pay2share.R
import com.pay2share.databinding.ActivityGroupDetailBinding
import com.pay2share.database.DatabaseHelper
import com.pay2share.data.database.GrupoRepository
import com.pay2share.data.database.UsuarioRepository
import com.pay2share.data.database.GastoRepository

class GroupDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroupDetailBinding
    private lateinit var grupoRepository: GrupoRepository
    private lateinit var usuarioRepository: UsuarioRepository
    private lateinit var gastoRepository: GastoRepository
    private var isCreator: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dbHelper = DatabaseHelper(this)
        grupoRepository = GrupoRepository(dbHelper)
        usuarioRepository = UsuarioRepository(dbHelper)
        gastoRepository = GastoRepository(dbHelper)

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
            Toast.makeText(this, "Expense added to group", Toast.LENGTH_SHORT).show()
        }
    }
}