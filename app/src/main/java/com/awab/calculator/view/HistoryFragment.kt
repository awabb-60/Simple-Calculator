package com.awab.calculator.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
        val view = inflater.inflate(R.layout.fragment_history, container, false)
        viewModel = ViewModelProvider(requireActivity())[CalculatorViewModel::class.java]

        val rv = view.findViewById<RecyclerView>(R.id.rvHistory)
        val btnClear = view.findViewById<Button>(R.id.btnClearHistory)

        //  clearing the history items database and close the fragment
        btnClear.setOnClickListener {
            listener.updateKeyPad()
            viewModel.clearHistoryItems()
        }

        val adapter = HistoryAdapter()
        adapter.setClickListener(this)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(context).apply {
            //  to stack from the bottom
            stackFromEnd = true
        }
        viewModel.historyItems.observe(viewLifecycleOwner,{
            adapter.submitList(it)

            //  to scroll to the new added item... 0 because the rv stack from the  end
            // TODO: 1/7/2022 scroll to the new added item
            if (it.isEmpty()){
                btnClear.visibility = View.GONE
            }else{
                btnClear.visibility = View.VISIBLE
            }
        })
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentListener) listener = context
    }

    override fun onItemClicked(equation:String, answer:String) {
        listener.typeHistoryItem(equation,answer)
    }

    /**
     * interface to communicate between History fragment and the activity
     */
    interface FragmentListener{
        /**
         * this will open or close this fragment
         */
        fun updateKeyPad()

        /**
         * this function will take put the history item data to the views with animations
         */
        fun typeHistoryItem(equation:String, answer:String)
    }
}