package com.digitaldream.winskool.adapters

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.winskool.R
import com.digitaldream.winskool.models.ClassNameTable
import java.util.*

class ReceiptsClassNameAdapter(
    private val sLevelList: MutableList<ClassNameTable>,
    private val sClassClick: OnClassClickListener,
) : RecyclerView.Adapter<ReceiptsClassNameAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.fragment_receipts_class_name_item, parent, false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val levelTable = sLevelList[position]
        holder.mClassName.text = levelTable.className

        val mutate = holder.mClassView.background.mutate() as GradientDrawable
        val random = Random()
        val currentColor = Color.argb(
            255, random.nextInt(256),
            random.nextInt(256), random.nextInt(256)
        )
        mutate.setColor(currentColor)
        holder.mClassView.background = mutate
    }

    override fun getItemCount() = sLevelList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mClassView: LinearLayout = itemView.findViewById(R.id.class_view)
        val mClassName: TextView = itemView.findViewById(R.id.class_name)

        init {
            itemView.setOnClickListener {
                sClassClick.onClassClick(adapterPosition)
            }
        }

    }
}
interface OnClassClickListener{
    fun onClassClick(position: Int)
}