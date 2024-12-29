package com.pay2share.ui.gallery

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.pay2share.R

class GroupDebtAdapter(private val context: Context, private val groupDebts: List<GroupDebt>) : BaseAdapter() {

    override fun getCount(): Int {
        return groupDebts.size
    }

    override fun getItem(position: Int): Any {
        return groupDebts[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_group_debt, parent, false)

        val groupDebt = groupDebts[position]
        val textViewGroupName = view.findViewById<TextView>(R.id.textViewGroupName)
        val textViewAmount = view.findViewById<TextView>(R.id.textViewAmount)

        textViewGroupName.text = groupDebt.groupName
        textViewAmount.text = "$${groupDebt.amount}"

        return view
    }
}