package com.rzahr.quicktools.views

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.rzahr.quicktools.R
import kotlinx.android.synthetic.main.rzuneditable.view.*

@Suppress("unused")
class QuickUnEditableView constructor(title: String, value: String, container: LinearLayout, context: Context) {

    var view: View

    init {

        @SuppressLint("InflateParams")
        view = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.rzuneditable, null)
        container.addView(view)

        view.title_tv.text = title
        view.value_tv.text = value
    }

    fun getValue(): TextView {

        return view.value_tv
    }
}