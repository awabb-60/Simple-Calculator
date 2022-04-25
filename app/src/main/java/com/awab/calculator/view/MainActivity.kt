package com.awab.calculator.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.children
import androidx.lifecycle.ViewModelProvider
import com.awab.calculator.R
import com.awab.calculator.databinding.ActivityMainBinding
import com.awab.calculator.viewmodels.CalculatorViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), HistoryFragment.FragmentListener {

    private lateinit var calculatorViewModel: CalculatorViewModel

    private lateinit var binding: ActivityMainBinding
    private lateinit var etType: EditText
    private lateinit var tvAnswer: TextView

    private val settingsActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            // updating the view if the settings has changed
            if (result.resultCode == Activity.RESULT_OK)
                recreate()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        calculatorViewModel = ViewModelProvider(this)[CalculatorViewModel::class.java]

        setCurrentSettings()

        binding = ActivityMainBinding.inflate(layoutInflater)
 
//        adjustViewsBaseOnScreenSize()

        setContentView(binding.root)

        etType = binding.etType
        tvAnswer = binding.tvAnswer

        // translation the fragment off the screen at the start
        val screenWidth = windowManager.defaultDisplay.width
        binding.historyFragment.translationX = -screenWidth.toFloat()

        // open or close the fragment
        binding.tvHistory.setOnClickListener {
            openCloseHistoryFragment()
        }

        // to put the answer text in the equation text
        tvAnswer.setOnClickListener {
            if (binding.etType.text.isEmpty())
                typeAnswer()
        }
        //  putting the observers to update the views
        calculatorViewModel.equationText.observe(this, { text ->
            etType.editableText.clear()
            etType.editableText.append(text)
        })
        calculatorViewModel.answerText.observe(this, { answer ->
            binding.tvAnswer.text = answer
        })
        calculatorViewModel.cursorPosition.observe(this, { pos ->
            etType.setSelection(pos)
        })
        calculatorViewModel.errorMessage.observe(this, { message ->
            if(message.isNotEmpty())
                Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
        })

        removeInputMethod()

        setClickListenersOnTheButtons()

        // set click listener to allow thw user to change the cursor position
        etType.setOnClickListener {
            // no need to refactor at the start
            if (etType.selectionStart > 0)
                calculatorViewModel.refactorCursorPosition(etType.selectionStart - 1)
        }

        binding.openSettings.setOnClickListener {
            settingsActivityLauncher.launch(Intent(this@MainActivity, SettingsActivity::class.java))
        }
    }

    private fun adjustViewsBaseOnScreenSize() {
        val screenHeight = windowManager.defaultDisplay.height
        binding.typeLayout.layoutParams.height = (screenHeight * 0.3).toInt()
    }

    override fun onBackPressed() {
        // close the history items fragment when opened
        if (calculatorViewModel.historyFragmentActive) {
            openCloseHistoryFragment()
        } else super.onBackPressed()
    }

    override fun updateKeyPad() {
        openCloseHistoryFragment()
    }

    override fun typeHistoryItem(equation: String, answer: String) {
        // translating the view down
        etType.translationY = etType.height.toFloat() / 2
        etType.alpha = 0F
        tvAnswer.translationY = tvAnswer.height.toFloat()

        // putting the history item data
        calculatorViewModel.updateEquation(equation)
        calculatorViewModel.updateAnswer(answer)

        // puling back the views
        etType.animate().setDuration(300).alpha(1F).translationY(0F).start()
        tvAnswer.animate().setDuration(300).translationY(0F).start()
    }

    private fun setCurrentSettings() {
        if (calculatorViewModel.getSavedDarkModeState(this))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        setTheme(calculatorViewModel.getSavedTheme(this).resId)
    }

    private fun removeInputMethod() {
        // to remove the input method from the screen at all time
        etType.inputType = InputType.TYPE_NULL
        etType.setRawInputType(InputType.TYPE_CLASS_TEXT)
        etType.setTextIsSelectable(true)
    }

    private fun setClickListenersOnTheButtons() {
        // lopping throe the rows
        binding.keyPadLayout.children.forEach { tableRow->
            // double check that its a TableRow
            if (tableRow is TableRow) {
                // set a click listener for every child (button)
                tableRow.children.forEach { button->
                    button.setOnClickListener {
                        buttonClicked(it)
                    }
                }
            }
        }
    }

    /**
     * this function will take the answer and put it in the equation text with animations
     */
    private fun typeAnswer() {
        // translating tvType up and off the screen then set the answer
        etType.translationY = (-etType.height).toFloat()

        // put the answer in the type
        calculatorViewModel.answerClicked()

        // translating tvAnswer down and then back to it position
        tvAnswer.animate().setDuration(300).translationY(tvAnswer.height.toFloat()).start()
        tvAnswer.postDelayed({ tvAnswer.translationY = 0F;calculatorViewModel.updateAnswer("") }, 400)

        // pull tvType back
        etType.animate().setDuration(300).translationY(0F).start()
    }

    /**
     * open and close the history fragment with animations
     */
    private fun openCloseHistoryFragment() {
        calculatorViewModel.historyFragmentActive = if (!calculatorViewModel.historyFragmentActive) {
            // show the history fragment
            binding.tvHistory.setText(R.string.keyPad)
            binding.historyFragment.animate().setDuration(300).translationX(0F).start()

            // making the fragment clickable so the click event doesn't go to the keyPad
            binding.historyFragment.isClickable = true
            true
        } else {
            // un show the history fragment
            binding.tvHistory.setText(R.string.history)
            binding.historyFragment.animate().setDuration(300)
                .translationX(-binding.historyFragment.width.toFloat())
                .start()

            // taking the focus form the fragment to start using the keyPad
            binding.historyFragment.isClickable = false
            false
        }
    }

    /**
     * all the keyPad buttons will trigger this function
     */
    private fun buttonClicked(v: View) {
        when (v.id) {
            R.id.clearAll -> clearAll()
            R.id.equals -> equals()
            else -> calculatorViewModel.buttonClicked(v.id, etType.selectionStart, etType.selectionEnd)
        }
    }

    /**
     * this will start making the calculations and do some animations
     */
    private fun equals() {
        if (calculatorViewModel.makeCalculations()) {
            etType.animate().setDuration(200).alpha(0F).start()
            etType.postDelayed({ etType.alpha = 1F;calculatorViewModel.updateEquation("") }, 300)
        }
    }

    /**
     * the function will close the history items fragment and
     * use the View Model to clearing the history items database
     */
    private fun clearAll() {
        etType.animate().setDuration(200).alpha(0F).start()
        tvAnswer.animate().setDuration(200).alpha(0F).start()
        etType.postDelayed({ calculatorViewModel.clearAll();etType.alpha = 1F;tvAnswer.alpha = 1F }, 300)
    }
}