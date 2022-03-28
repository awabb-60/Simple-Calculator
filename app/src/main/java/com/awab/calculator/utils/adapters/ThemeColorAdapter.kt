package com.awab.calculator.utils.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.awab.calculator.data.data_models.ThemeModel
import com.awab.calculator.databinding.ThemeColorBinding

class ThemeColorAdapter(
    val context:Context,
    val list: List<ThemeModel>,
    val listener: ThemeColorListener
) : RecyclerView.Adapter<ThemeColorAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ThemeColorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(private val binding: ThemeColorBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.color.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION)
                    listener.onColorSelected(list[adapterPosition])
            }
        }

        fun bind(themeColor: ThemeModel) {
            binding.color.setCardBackgroundColor(ContextCompat.getColor(context, themeColor.color))
        }
    }

    interface ThemeColorListener{
        fun onColorSelected(theme:ThemeModel)
    }

}