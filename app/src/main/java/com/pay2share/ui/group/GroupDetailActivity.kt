package com.pay2share.ui.group

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pay2share.databinding.ActivityGroupDetailBinding
import com.pay2share.database.DatabaseHelper
import com.pay2share.data.database.GrupoRepository
import com.pay2share.data.database.UsuarioRepository

class GroupDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroupDetailBinding
    private lateinit var grupoRepository: GrupoRepository
    private lateinit var usuarioRepository: UsuarioRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dbHelper = DatabaseHelper(this)
        grupoRepository = GrupoRepository(dbHelper)
        usuarioRepository = UsuarioRepository(dbHelper)

        val groupId = intent.getIntExtra("GROUP_ID", -1)
        if (groupId != -1) {
            val groupCursor = grupoRepository.obtenerGrupoById(groupId)
            if (groupCursor.moveToFirst()) {
                val groupName = groupCursor.getString(groupCursor.getColumnIndexOrThrow("name"))
                binding.textGroupName.text = groupName
            }
            groupCursor.close()

            val participantsCursor = grupoRepository.obtenerParticipantesPorGrupo(groupId)
            val participants = mutableListOf<String>()
            while (participantsCursor.moveToNext()) {
                val participantName = participantsCursor.getString(participantsCursor.getColumnIndexOrThrow("name"))
                participants.add(participantName)
            }
            participantsCursor.close()
            binding.textParticipants.text = participants.joinToString("\n")

            val totalDebt = grupoRepository.obtenerTotalDeudaPorGrupo(groupId)
            binding.textTotalDebt.text = totalDebt.toString()
        }
    }
}