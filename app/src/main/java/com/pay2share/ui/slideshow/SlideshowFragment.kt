package com.pay2share.ui.slideshow

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.pay2share.R
import com.pay2share.data.database.ContactRepository
import com.pay2share.database.DatabaseHelper
import com.pay2share.databinding.FragmentSlideshowBinding

class SlideshowFragment : Fragment() {

    private var _binding: FragmentSlideshowBinding? = null
    private val binding get() = _binding!!

    private lateinit var contactRepository: ContactRepository
    private lateinit var contacts: MutableList<String>
    private lateinit var adapter: ContactAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dbHelper = DatabaseHelper(requireContext())
        contactRepository = ContactRepository(dbHelper)

        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val listViewContacts: ListView = binding.listViewContacts

        val sharedPreferences = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", -1)

        contacts = contactRepository.getContactsByUser(userId).toMutableList()
        adapter = ContactAdapter(requireContext(), contacts) { contactEmail ->
            contactRepository.deleteContact(userId, contactEmail)
            contacts.remove(contactEmail)
            adapter.notifyDataSetChanged()
            Toast.makeText(requireContext(), "Contacto eliminado", Toast.LENGTH_SHORT).show()
        }
        listViewContacts.adapter = adapter

        binding.buttonAddContact.setOnClickListener {
            val intent = Intent(requireContext(), AddContactActivity::class.java)
            startActivity(intent)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}