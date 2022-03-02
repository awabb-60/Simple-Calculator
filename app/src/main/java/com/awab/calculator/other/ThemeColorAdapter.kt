package com.awab.calculator.other

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.awab.calculator.data.data_models.ThemeColor
import com.awab.calculator.databinding.ThemeColorBinding

class ThemeColorAdapter(
    val list: List<ThemeColor>,
    val onClick: (Int) -> Unit
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
                    onClick(list[adapterPosition].themeIndex)
            }
        }

        fun bind(themeColor: ThemeColor) {
            binding.color.setCardBackgroundColor(themeColor.color)
        }
    }
}