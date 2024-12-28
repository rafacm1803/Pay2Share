package com.pay2share.ui.group

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.pay2share.R
import com.pay2share.database.DatabaseHelper
import com.pay2share.data.database.GrupoRepository
import com.pay2share.data.database.UsuarioRepository

class UserDetailActivity : AppCompatActivity() {

    private lateinit var grupoRepository: GrupoRepository
    private lateinit var usuarioRepository: UsuarioRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_detail)

        val dbHelper = DatabaseHelper(this)
        grupoRepository = GrupoRepository(dbHelper)
        usuarioRepository = UsuarioRepository(dbHelper)

        val userId = intent.getIntExtra("USER_ID", -1)
        val groupId = intent.getIntExtra("GROUP_ID", -1)

        if (userId != -1 && groupId != -1) {
            val userCursor = usuarioRepository.obtenerUsuarioPorId(userId)
            if (userCursor.moveToFirst()) {
                val userName = userCursor.getString(userCursor.getColumnIndexOrThrow("name"))
                val textViewUserDebtInfo: TextView = findViewById(R.id.textViewUserDebtInfo)
                textViewUserDebtInfo.text = "La deuda de $userName es..."
            }
            userCursor.close()

            val debt = grupoRepository.obtenerDeudaPorUsuarioYGrupo(userId, groupId)
            val textViewDebt: TextView = findViewById(R.id.textViewDebt)
            textViewDebt.text = "Debt: $$debt"

            if (debt > 0) {
                textViewDebt.setTextColor(resources.getColor(android.R.color.holo_red_dark))
            } else {
                textViewDebt.setTextColor(resources.getColor(android.R.color.holo_green_dark))
            }
        }
    }
}