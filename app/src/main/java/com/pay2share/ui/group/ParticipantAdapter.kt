package com.pay2share.ui.group

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.pay2share.R

data class Participant(val name: String, val debt: Double)

class ParticipantAdapter(private val context: Context, private val participants: List<Participant>) : BaseAdapter() {

    override fun getCount(): Int {
        return participants.size
    }

    override fun getItem(position: Int): Any {
        return participants[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_participant, parent, false)

        val participant = participants[position]
        val textViewName = view.findViewById<TextView>(R.id.textViewParticipantName)
        val textViewDebt = view.findViewById<TextView>(R.id.textViewParticipantDebt)
        val imageViewAvatar = view.findViewById<ImageView>(R.id.imageViewAvatar)

        textViewName.text = participant.name
        val debtText = if (participant.debt > 0) {
            "<font color='#FF0000'>\$${participant.debt}</font>"
        } else {
            "<font color='#00FF00'>\$${participant.debt}</font>"
        }
        textViewDebt.text = Html.fromHtml(debtText, Html.FROM_HTML_MODE_LEGACY)
        imageViewAvatar.setImageResource(R.drawable.ic_avatar) // Replace with your avatar drawable

        return view
    }
}