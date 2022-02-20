package com.awab.calculator.view

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.awab.calculator.R
import com.awab.calculator.databinding.ActivityMainBinding
import com.awab.calculator.viewmodels.CalculatorViewModel

// infinity
// see -0
class MainActivity : AppCompatActivity(), HistoryFragment.FragmentListener {

    private lateinit var viewModel: CalculatorViewModel

    private lateinit var binding: ActivityMainBinding
    private lateinit var etType: EditText
    private lateinit var tvAnswer: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        etType = binding.etType
        tvAnswer = binding.tvAnswer

        disableTheInputMethod()

        //  translation the fragment off the screen at the start
        val screenWidth = windowManager.defaultDisplay.width
        binding.historyFragment.translationX = -screenWidth.toFloat()

        //  open or close the fragment
        binding.tvHistory.setOnClickListener {
            openCloseHistoryFragment()
        }

        // start the calculations
        binding.btnEquals.setOnClickListener { equals() }

        viewModel = ViewModelProvider(this)[CalculatorViewModel::class.java]
        etType.setText(viewModel.equationText.value)
        tvAnswer.text = viewModel.answerText.value

        // to put the answer text in the equation text
        tvAnswer.setOnClickListener {
            if (binding.etType.text.isEmpty())
                typeAnswer()
        }
        //  putting the observers to update the views
        viewModel.equationText.observe(this, { t ->
            etType.editableText.clear()
            etType.editableText.append(t)
        })
        viewModel.courserPosition.observe(this, { pos ->
            etType.setSelection(pos)
        })

        viewModel.answerText.observe(this, { t ->
            binding.tvAnswer.text = t
        })
    }

    private fun disableTheInputMethod() {
        // remove the input method when clicked
        etType.setOnClickListener {

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(etType.windowToken, 0)
        }
    }

    override fun onBackPressed() {
        //  close the history items fragment when opened
        if (viewModel.historyFragmentActive) {
            openCloseHistoryFragment()
        } else super.onBackPressed()
    }

    override fun updateKeyPad() {
        openCloseHistoryFragment()
    }

    override fun typeHistoryItem(equation: String, answer: String) {
        //  translating the view down
        etType.translationY = etType.height.toFloat() / 2
        etType.alpha = 0F
        tvAnswer.translationY = tvAnswer.height.toFloat()

        //  putting the history item data
        viewModel.updateEquation(equation)
        viewModel.updateAnswer(answer)

        //  puling back the views
        etType.animate().setDuration(300).alpha(1F).translationY(0F).start()
        tvAnswer.animate().setDuration(300).translationY(0F).start()
    }

    /**
     * this function will take the answer and put it in the equation text with animations
     */
    private fun typeAnswer() {
        //  translating tvType up and off the screen then set the answer
        etType.translationY = (-etType.height).toFloat()

        // put the answer in the type
        viewModel.answerClicked()

        //  translating tvAnswer down and then back to it position
        tvAnswer.animate().setDuration(300).translationY(tvAnswer.height.toFloat()).start()
        tvAnswer.postDelayed({ tvAnswer.translationY = 0F;viewModel.updateAnswer("") }, 400)

        //  pull tvType back
        etType.animate().setDuration(300).translationY(0F).start()
    }

    /**
     * open and close the history fragment with animations
     */
    private fun openCloseHistoryFragment() {
        viewModel.historyFragmentActive = if (!viewModel.historyFragmentActive) {
            // show the history fragment
            binding.tvHistory.setText(R.string.keyPad)
            binding.historyFragment.animate().setDuration(300).translationX(0F).start()

            // making the fragment clickable so the click event doesn't go to the keyPad
            binding.historyFragment.isClickable = true
            true
        } else {
            // un show the history fragment
            binding.tvHistory.setText(R.string.history)
            binding.historyFragment.animate().setDuration(300).translationX(-binding.historyFragment.width.toFloat())
                .start()

            // taking the focus form the fragment to start using the keyPad
            binding.historyFragment.isClickable = false
            false
        }
    }

    /**
     * all the keyPad buttons will trigger this function
     */
    fun buttonClicked(v: View) {
        when (v.id) {
            R.id.clearAll -> clearAll()
            else -> viewModel.buttonClicked(v.id, etType.selectionStart)
        }
    }

    // TODO: 1/6/2022 veiled
    /**
     * this will start making the calculations and do some animations to the type veiled
     */
    private fun equals() {
        if (viewModel.makeCalculations(this)) {
            etType.animate().setDuration(200).alpha(0F).start()
            etType.postDelayed({ etType.alpha = 1F;viewModel.updateEquation("") }, 300)
        }
    }

    /**
     * the function will close the history items fragment and
     * use the View Model to clearing the history items database
     */
    private fun clearAll() {
        etType.animate().setDuration(200).alpha(0F).start()
        tvAnswer.animate().setDuration(200).alpha(0F).start()
        etType.postDelayed({ viewModel.clearAll();etType.alpha = 1F;tvAnswer.alpha = 1F }, 300)
    }
}