package com.pay2share.ui.slideshow

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import com.pay2share.R

class ContactAdapter(private val context: Context, private val contacts: List<String>, private val onDeleteClick: (String) -> Unit) : BaseAdapter() {

    override fun getCount(): Int {
        return contacts.size
    }

    override fun getItem(position: Int): Any {
        return contacts[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false)

        val contactEmail = contacts[position]
        val textViewContactEmail = view.findViewById<TextView>(R.id.textViewContactEmail)
        val buttonDeleteContact = view.findViewById<Button>(R.id.buttonDeleteContact)

        textViewContactEmail.text = contactEmail
        buttonDeleteContact.setOnClickListener {
            onDeleteClick(contactEmail)
        }

        return view
    }
}