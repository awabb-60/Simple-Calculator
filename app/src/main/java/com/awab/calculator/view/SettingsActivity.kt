package com.awab.calculator.view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.awab.calculator.R
import com.awab.calculator.databinding.ActivitySettingsBinding
import com.awab.calculator.databinding.PickThemeColorLayoutBinding
import com.awab.calculator.other.ThemeColorAdapter

class SettingsActivity : AppCompatActivity() {

    private var darkModeState = false
    private var themeColorIndex = 0

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // this has to before inflating the layout
        setCurrentThemeColor()
        binding = ActivitySettingsBinding.inflate(layoutInflater)

        displayTheCurrentSettings()
        setContentView(binding.root)


        binding.fabSave.setOnClickListener {
            setResult(RESULT_OK)
            saveSettings()
            Toast.makeText(this@SettingsActivity, "settings saved", Toast.LENGTH_SHORT).show()
        }

        binding.themeColor.setOnClickListener {
            openThemeColorDialog()
        }

        binding.darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            darkModeState = isChecked
        }
    }

    private fun setCurrentThemeColor() {
        val sp = getSharedPreferences(SETTINGS_SHARED_PREFERENCES, MODE_PRIVATE)

        val darkModeState = sp.getBoolean(CURRENT_DARK_MODE_STATE, false)
        if (darkModeState)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val currentThemeColor = when (sp.getInt(CURRENT_THEME_INDEX, 0)) {
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

    private fun openThemeColorDialog() {
        val dialogBinding = PickThemeColorLayoutBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(this).setView(dialogBinding.root)
            .create()

        // the action after the color is clicked
        val colorOnClick: (Int) -> Unit = { themeIndex ->
            themeColorIndex = themeIndex
            val newTheme =
                AVAILABLE_THEME_COLORS.find { it.themeIndex == themeIndex }
                    ?: AVAILABLE_THEME_COLORS[DEFAULT_THEME_INDEX]
            binding.themeColorRect.setBackgroundColor(ContextCompat.getColor(this, newTheme.colorResId))
            dialog.cancel()
        }

        // the color list rv
        dialogBinding.rvColors.adapter =
            ThemeColorAdapter(this@SettingsActivity, AVAILABLE_THEME_COLORS, colorOnClick)
        dialogBinding.rvColors.layoutManager = GridLayoutManager(this, 3)
        dialogBinding.rvColors.setHasFixedSize(true)

        // removing the white background
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun displayTheCurrentSettings() {
        val settingsSP = getSharedPreferences(SETTINGS_SHARED_PREFERENCES, MODE_PRIVATE)

        // getting and displaying the current saved state of the dark mode
        binding.darkModeSwitch.isChecked = settingsSP.getBoolean(CURRENT_DARK_MODE_STATE, DEFAULT_DARK_MODE_STATE)
        darkModeState = binding.darkModeSwitch.isChecked

        // getting and displaying the current saved theme index color
        val currentThemeIndex = settingsSP.getInt(CURRENT_THEME_INDEX, DEFAULT_THEME_INDEX)
        themeColorIndex = currentThemeIndex
    }

    private fun saveSettings() {
        val spEditor = getSharedPreferences(SETTINGS_SHARED_PREFERENCES, MODE_PRIVATE).edit()
        spEditor.putBoolean(CURRENT_DARK_MODE_STATE, darkModeState)
        spEditor.putInt(CURRENT_THEME_INDEX, themeColorIndex)
        spEditor.apply()
    }
}