package com.awab.calculator.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.awab.calculator.utils.adapters.HistoryAdapter
import com.awab.calculator.databinding.FragmentHistoryBinding
import com.awab.calculator.viewmodels.CalculatorViewModel

class HistoryFragment : Fragment(), HistoryAdapter.HistoryClickListener {

    private lateinit var calculatorViewModel:CalculatorViewModel
    private lateinit var listener:FragmentListener
    private var _binding: FragmentHistoryBinding? = null

    private val binding:FragmentHistoryBinding
    get() = _binding!!


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentListener) listener = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        calculatorViewModel = ViewModelProvider(requireActivity())[CalculatorViewModel::class.java]

        val rv = binding.rvHistory
        val btnClear = binding.btnClearHistory

        //  clearing the history items database and close the fragment
        btnClear.setOnClickListener {
            listener.updateKeyPad()
            calculatorViewModel.clearHistoryItems()
        }

        val adapter = HistoryAdapter()
        adapter.setClickListener(this)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(context).apply {
            //  to stack from the bottom
            stackFromEnd = true
        }
        calculatorViewModel.historyItems.observe(viewLifecycleOwner,{
            adapter.submitList(it)

            // scrolling to the last / bottom history item
            if (it.isNotEmpty())
                rv.smoothScrollToPosition(it.indices.last)

            if (it.isEmpty()){
                // removing the rv and the clear button
                btnClear.visibility = View.GONE
            }else{
                // showing the rv and the clear button
                btnClear.visibility = View.VISIBLE
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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