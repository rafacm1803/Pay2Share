package com.pay2share.ui.group

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
            val participants = mutableListOf<String>()
            val participantIds = mutableListOf<Int>()
            while (participantsCursor.moveToNext()) {
                val participantName = participantsCursor.getString(participantsCursor.getColumnIndexOrThrow("name"))
                val participantId = participantsCursor.getInt(participantsCursor.getColumnIndexOrThrow("id"))
                participants.add(participantName)
                participantIds.add(participantId)
            }
            participantsCursor.close()
            binding.textParticipants.text = participants.joinToString("\n")

            val totalDebt = grupoRepository.obtenerTotalDeudaPorGrupo(groupId)
            binding.textTotalDebt.text = totalDebt.toString()

            val listViewParticipants: ListView = findViewById(R.id.listViewParticipants)
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, participants)
            listViewParticipants.adapter = adapter

            listViewParticipants.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                val intent = Intent(this, UserDetailActivity::class.java)
                intent.putExtra("USER_ID", participantIds[position])
                intent.putExtra("GROUP_ID", groupId)
                startActivity(intent)
            }
        }

        binding.buttonAddUser.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
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
            if (expenseName.isNotEmpty() && expenseAmount != null) {
                val date = System.currentTimeMillis().toString() // You can format this as needed
                gastoRepository.crearGasto(expenseName, expenseAmount, date, userId.toString(), groupId)
                Toast.makeText(this, "Expense added to group", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Invalid expense details", Toast.LENGTH_SHORT).show()
            }
        }

        if (isCreator) {
            binding.editTextDebtAmount.visibility = View.VISIBLE
            binding.buttonAssignDebt.visibility = View.VISIBLE

            binding.buttonAssignDebt.setOnClickListener {
                val debtAmount = binding.editTextDebtAmount.text.toString().toDoubleOrNull()
                if (debtAmount != null) {
                    val email = binding.editTextEmail.text.toString()
                    val userCursor = usuarioRepository.obtenerUsuarioPorEmail(email)
                    if (userCursor != null && userCursor.moveToFirst()) {
                        val userId = userCursor.getInt(userCursor.getColumnIndexOrThrow("id"))
                        grupoRepository.asignarDeudaAGrupo(userId, groupId, debtAmount)
                        Toast.makeText(this, "Debt assigned to user", Toast.LENGTH_SHORT).show()
                        userCursor.close()
                    } else {
                        Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Invalid debt amount", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}