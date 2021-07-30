package com.funapps.spainiptv.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.funapps.spainiptv.R


class SpinnerAdapter(private val mContext: Context, private val resource: Int, private val list: ArrayList<String>): ArrayAdapter<String>(mContext, resource, 0, list) {

    private var mInflater: LayoutInflater = LayoutInflater.from(mContext)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent);
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent);
    }

    private fun createItemView(position: Int, convertView: View?, parent: ViewGroup): View{
        val view: View = mInflater.inflate(resource, parent, false)

        val spinnerItemName = view.findViewById<View>(R.id.spinner_name) as TextView

        spinnerItemName.text = list[position]

        return view
    }
}