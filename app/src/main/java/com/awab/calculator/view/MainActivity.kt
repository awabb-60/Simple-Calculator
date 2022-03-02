package com.awab.calculator.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.InputType
import android.view.GestureDetector
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.awab.calculator.R
import com.awab.calculator.data.data_models.ThemeColor
import com.awab.calculator.databinding.ActivityMainBinding
import com.awab.calculator.databinding.PickThemeColorLayoutBinding
import com.awab.calculator.other.ThemeColorAdapter
import com.awab.calculator.viewmodels.CalculatorViewModel

class MainActivity : AppCompatActivity(), HistoryFragment.FragmentListener {

    private lateinit var viewModel: CalculatorViewModel

    private lateinit var binding: ActivityMainBinding
    private lateinit var etType: EditText
    private lateinit var tvAnswer: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCurrentThemeColor()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        etType = binding.etType
        tvAnswer = binding.tvAnswer

        //  translation the fragment off the screen at the start
        val screenWidth = windowManager.defaultDisplay.width
        binding.historyFragment.translationX = -screenWidth.toFloat()

        //  open or close the fragment
        binding.tvHistory.setOnClickListener {
            openCloseHistoryFragment()
        }

        viewModel = ViewModelProvider(this)[CalculatorViewModel::class.java]
        etType.setText(viewModel.equationText.value)
        tvAnswer.text = viewModel.answerText.value

        // to put the answer text in the equation text
        tvAnswer.setOnClickListener {
            if (binding.etType.text.isEmpty())
                typeAnswer()
        }
        //  putting the observers to update the views
        viewModel.equationText.observe(this, { text ->
            etType.editableText.clear()
            etType.editableText.append(text)
        })
        viewModel.cursorPosition.observe(this, { pos ->
            etType.setSelection(pos)
        })
        viewModel.answerText.observe(this, { answer ->
            binding.tvAnswer.text = answer
        })

        etType.inputType = InputType.TYPE_NULL
        etType.setRawInputType(InputType.TYPE_CLASS_TEXT)
        etType.setTextIsSelectable(true)

        etType.setOnClickListener {
            // no need to refactor at the start
            if (etType.selectionStart > 0)
                viewModel.refactorCursorPosition(etType.selectionStart - 1)
        }


        binding.ivChangeThemeColor.setOnClickListener {
            val dialogBinding = PickThemeColorLayoutBinding.inflate(layoutInflater)
            val dialog = AlertDialog.Builder(this).setView(dialogBinding.root)
                .create()

            val availableThemeColors = listOf(
                ThemeColor(0, ContextCompat.getColor(this@MainActivity, R.color.theme_color_0)),
                ThemeColor(1, ContextCompat.getColor(this@MainActivity, R.color.theme_color_1)),
                ThemeColor(2, ContextCompat.getColor(this@MainActivity, R.color.theme_color_2)),
                ThemeColor(3, ContextCompat.getColor(this@MainActivity, R.color.theme_color_3)),
                ThemeColor(4, ContextCompat.getColor(this@MainActivity, R.color.theme_color_4)),
                ThemeColor(5, ContextCompat.getColor(this@MainActivity, R.color.theme_color_5)),
                ThemeColor(6, ContextCompat.getColor(this@MainActivity, R.color.theme_color_6)),
                ThemeColor(7, ContextCompat.getColor(this@MainActivity, R.color.theme_color_7)),
                ThemeColor(8, ContextCompat.getColor(this@MainActivity, R.color.theme_color_8)),
            )

            // the action after the color is clicked
            val colorOnClick: (Int) -> Unit = { themeIndex ->
                changeThemeColor(themeIndex)
                dialog.cancel()
            }

            // the color list rv
            dialogBinding.rvColors.adapter = ThemeColorAdapter(availableThemeColors, colorOnClick)
            dialogBinding.rvColors.layoutManager = GridLayoutManager(this, 3)
            dialogBinding.rvColors.setHasFixedSize(true)

            // removing the white background
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
        }
    }

    private fun setCurrentThemeColor() {
        val sp = getSharedPreferences(THEME_SHARED_PREFERENCES, MODE_PRIVATE)
        val currentThemeColor = when (sp.getInt(CURRENT_THEME, 0)) {
            1 -> R.style.theme1
            2 -> R.style.theme2
            3 -> R.style.theme3
            4 -> R.style.theme4
            5 -> R.style.theme5
            6 -> R.style.theme6
            7 -> R.style.theme7
            8 -> R.style.theme8
            else -> R.style.Theme_Calculator
        }
        setTheme(currentThemeColor)
    }

    private fun changeThemeColor(themeIndex: Int) {
        // saving the new theme color  index
        if(saveThemeColor(themeIndex)){
            // refreshing the activity
            recreate()
        }
    }

    /**
     * @return true if the theme index has been updated. false if the the theme index has has not been saved
     * or the old value is the same
     */
    private fun saveThemeColor(themeIndex: Int): Boolean {
        val sp = getSharedPreferences(THEME_SHARED_PREFERENCES, MODE_PRIVATE)
        return if (sp.getInt(CURRENT_THEME, 0) != themeIndex) {
            val spEditor = sp.edit()
            spEditor.putInt(CURRENT_THEME, themeIndex)
            spEditor.apply()
            true
        } else
            false
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
    fun buttonClicked(v: View) {
        when (v.id) {
            R.id.clearAll -> clearAll()
            R.id.equals -> equals()
            else -> viewModel.buttonClicked(v.id, etType.selectionStart, etType.selectionEnd)
        }
    }

    /**
     * this will start making the calculations and do some animations
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