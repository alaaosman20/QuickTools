package com.rzahr.quicktools.adaptors

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.rzahr.quicktools.R

class QuickColorListAdapter  constructor (clickHandler: QuickColorListAdapterOnClickHandler): RecyclerView.Adapter<QuickColorListAdapter.ViewHolder>() {

    companion object {

        private var mClickHandler: QuickColorListAdapterOnClickHandler? = null
    }

    private val colors = intArrayOf(
        Color.MAGENTA,
        Color.RED,
        Color.CYAN,
        Color.YELLOW,
        Color.GREEN,
        Color.BLACK,
        Color.LTGRAY,
        Color.GRAY
    )

    init {
        mClickHandler = clickHandler
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_color_list, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) holder.colorButton.backgroundTintList = ColorStateList.valueOf(colors[position])
    }

    override fun getItemCount(): Int {

        return colors.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var colorButton = itemView.findViewById<ImageButton>(R.id.color_item)!!

        init {
            itemView.setOnClickListener { mClickHandler?.onColorSelected(colors[adapterPosition]) }
        }
    }
}

interface QuickColorListAdapterOnClickHandler {

    fun onColorSelected(color: Int)

}