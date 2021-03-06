package com.awab.calculator.utils.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.awab.calculator.R
import com.awab.calculator.data.local.room.entitys.HistoryItem

class HistoryAdapter: ListAdapter<HistoryItem, HistoryAdapter.ViewHolder>(DiffCallBack()) {
    private lateinit var listener: HistoryClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.history_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = getItem(position)
        holder.equation.text = item.equation
        holder.answer.text = item.answer
    }

    /**
     * set the click listener the will get used when interacting with items
     * @param listener of type HistoryClickListener
     * */
    fun setClickListener(listener: HistoryClickListener){
        this.listener = listener
    }

    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val equation: TextView = view.findViewById(R.id.tvHistoryItemE)
        val answer: TextView = view.findViewById(R.id.tvHistoryItemAnswer)
        init {
            view.setOnClickListener {
                val item = getItem(adapterPosition)
                listener.onItemClicked(item.equation, item.answer)
            }
        }
    }


    class DiffCallBack:DiffUtil.ItemCallback<HistoryItem>(){
        override fun areItemsTheSame(oldItem: HistoryItem, newItem: HistoryItem) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: HistoryItem, newItem: HistoryItem) =
            oldItem == newItem
    }

    interface HistoryClickListener{
        fun onItemClicked(equation:String, answer:String)
    }
}