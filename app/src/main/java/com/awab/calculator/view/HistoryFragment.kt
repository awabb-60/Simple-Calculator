package com.awab.calculator.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.awab.calculator.other.HistoryAdapter
import com.awab.calculator.R
import com.awab.calculator.viewmodels.CalculatorViewModel

class HistoryFragment : Fragment(), HistoryAdapter.HistoryClickListener {

    private lateinit var viewModel:CalculatorViewModel
    private lateinit var listener:FragmentListener
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[CalculatorViewModel::class.java]

        val rv = view.findViewById<RecyclerView>(R.id.rvHistory)
        val ivEmpty = view.findViewById<ImageView>(R.id.ivEmpty)
        val tvClear = view.findViewById<TextView>(R.id.tvClearHistory)

        tvClear.setOnClickListener {
            viewModel.clearHistoryItems()
            listener.update()
        }

        val adapter = HistoryAdapter()
        viewModel.historyItems.observe(viewLifecycleOwner,{
             adapter.submitList(it)
            if (it.isEmpty()){
                rv.visibility = View.GONE
                tvClear.visibility = View.GONE
                ivEmpty.visibility = View.VISIBLE
            }else{
                rv.visibility = View.VISIBLE
                tvClear.visibility = View.VISIBLE
                ivEmpty.visibility = View.GONE
            }
        })
        adapter.setClickListener(this)
        rv.adapter = adapter

//        to make start from the bottom
        rv.layoutManager = LinearLayoutManager(context).also { it.stackFromEnd = true }
        rv.setHasFixedSize(true)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentListener) listener = context
    }

    override fun onItemClicked(equation:String, answer:String) {
        viewModel.updateEquation(equation)
        viewModel.updateAnswer(answer)
    }


    interface FragmentListener{fun update()}
}