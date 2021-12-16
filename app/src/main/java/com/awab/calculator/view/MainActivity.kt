package com.awab.calculator.view

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.awab.calculator.R
import com.awab.calculator.utils.*
import com.awab.calculator.viewmodels.CalculatorViewModel


class MainActivity : AppCompatActivity(),HistoryFragment.FragmentListener {

    private lateinit var tvType: TextView
    private lateinit var tvAnswer: TextView

    private lateinit var btnEquals: Button

    private lateinit var viewModel: CalculatorViewModel

    private lateinit var fragmentContainer: View

    private lateinit var tvHistory: TextView

    private var historyFragmentActive = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        tvType = findViewById(R.id.tvType)
        tvAnswer = findViewById(R.id.tvAnswer)

        fragmentContainer = findViewById(R.id.historyFragment)

        val screenWidth = windowManager.defaultDisplay.width
        fragmentContainer.translationX = -screenWidth.toFloat()
        tvHistory = findViewById(R.id.tvHistory)
        tvHistory.setOnClickListener {
            historyFragmentAnimations()

        }

        btnEquals = findViewById(R.id.equals)
        btnEquals.setOnClickListener { equals() }

        viewModel = ViewModelProvider(this)[CalculatorViewModel::class.java]
        tvType.text = viewModel.equationText.value
        tvAnswer.text = viewModel.answerText.value

        viewModel.equationText.observe(this, object : Observer<String> {
            override fun onChanged(t: String?) {
                tvType.text = t
            }
        })
        viewModel.answerText.observe(this, object : Observer<String> {
            override fun onChanged(t: String?) {
                tvAnswer.text = t
            }
        })
    }


    override fun onBackPressed() {
        if(historyFragmentActive){
            historyFragmentAnimations()
        }else super.onBackPressed()
    }

    override fun update() {
        historyFragmentAnimations()
    }

//    control the fragment animations
    private fun historyFragmentAnimations() {
        historyFragmentActive = if (!historyFragmentActive) {
            tvHistory.setText(R.string.keyPad)
            fragmentContainer.animate().setDuration(300).translationX(0F).start()
            true
        } else {
            tvHistory.setText(R.string.history)
            fragmentContainer.animate().setDuration(300).translationX(-fragmentContainer.width.toFloat())
                .start()
            false
        }
    }

//    all the keyPad buttons will trigger this function
    fun buttonClicked(v: View) {
        when (v.id) {
            R.id.decimalPoint -> {
                type('.')
            }
            R.id.number_0 -> {
                type('0')
            }
            R.id.number_1 -> {
                type('1')
            }
            R.id.number_2 -> {
                type('2')
            }
            R.id.number_3 -> {
                type('3')
            }
            R.id.number_4 -> {
                type('4')
            }
            R.id.number_5 -> {
                type('5')
            }
            R.id.number_6 -> {
                type('6')
            }
            R.id.number_7 -> {
                type('7')
            }
            R.id.number_8 -> {
                type('8')
            }
            R.id.number_9 -> {
                type('9')
            }
            R.id.addition -> {
                type(ADDITION_SIGN)
            }
            R.id.subtraction -> {
                type(SUBTRACTION_SIGN)
            }
            R.id.multiply -> {
                type(MULTIPLICATION_SIGN)
            }
            R.id.division -> {
                type(DIVISION_SIGN)
            }
            R.id.lParenthesis -> {
                type(LEFT_PARENTHESIS)
            }
            R.id.rParenthesis -> {
                type(RIGHT_PARENTHESIS)
            }
            R.id.negativeP -> {
                negativeParenthesis()
            }
            R.id.backSpace -> {
                viewModel.backSpace()
            }
            R.id.clearAll -> {
                clearAll()
            }
        }
    }

    private fun type(char: Char) {
        viewModel.updateEquation(filterInput(tvType.text.toString(), char))
    }

    private fun negativeParenthesis() {
        viewModel.updateEquation(filterInput(tvType.text.toString(), LEFT_PARENTHESIS))
        viewModel.updateEquation(filterInput(tvType.text.toString(), SUBTRACTION_SIGN))
    }

    private fun equals() {
        if (viewModel.makeCalculations(this)) {
            tvType.animate().setDuration(200).alpha(0F).start()
            tvType.postDelayed({ tvType.alpha = 1F;viewModel.updateEquation("") }, 300)
        }
    }

    private fun clearAll() {
        tvType.animate().setDuration(200).alpha(0F).start()
        tvType.postDelayed({ tvType.alpha = 1F;viewModel.clearAll() }, 300)
    }
}