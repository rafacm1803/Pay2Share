package com.pay2share.ui.gallery

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.pay2share.R
import com.pay2share.data.database.DeudaRepository
import com.pay2share.database.DatabaseHelper
import com.pay2share.databinding.FragmentGalleryBinding

data class GroupDebt(val groupName: String, val amount: Double)

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    private lateinit var deudaRepository: DeudaRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dbHelper = DatabaseHelper(requireContext())
        deudaRepository = DeudaRepository(dbHelper)

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textViewTotalDebt: TextView = binding.textViewTotalDebt
        val listViewGroupDebts: ListView = binding.listViewGroupDebts

        val sharedPreferences = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", -1)

        if (userId != -1) {
            val groupDebts = calculateGroupDebts(userId)
            val totalDebt = groupDebts.sumOf { it.amount }
            textViewTotalDebt.text = "Deuda Total: $${totalDebt}"

            val adapter = GroupDebtAdapter(requireContext(), groupDebts)
            listViewGroupDebts.adapter = adapter
        }

        return root
    }

    private fun calculateGroupDebts(userId: Int): List<GroupDebt> {
        val groupDebts = mutableListOf<GroupDebt>()
        val cursor = deudaRepository.obtenerDeudasPorUsuario(userId)
        while (cursor.moveToNext()) {
            val groupName = cursor.getString(cursor.getColumnIndexOrThrow("group_name"))
            val amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"))
            groupDebts.add(GroupDebt(groupName, amount))
        }
        cursor.close()
        return groupDebts
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}