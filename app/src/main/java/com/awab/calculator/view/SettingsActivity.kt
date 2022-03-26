package com.awab.calculator.view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.awab.calculator.databinding.ActivitySettingsBinding
import com.awab.calculator.databinding.PickThemeColorLayoutBinding
import com.awab.calculator.utils.adapters.ThemeColorAdapter
import com.awab.calculator.utils.calculator_utils.Calculator
import com.awab.calculator.viewmodels.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]

        if (savedInstanceState == null)
            setCurrentSettings()

        AppCompatDelegate.setDefaultNightMode(if(settingsViewModel.darkModeState)
            AppCompatDelegate.MODE_NIGHT_YES
        else
            AppCompatDelegate.MODE_NIGHT_NO)
        setTheme(settingsViewModel.themeRes.value!!)

        // inflating the layout
        binding = ActivitySettingsBinding.inflate(layoutInflater)

        // this has to before inflating the layout
        // only at the start of the activity
        // after that the view will get the saved settings from the viewModel

        // display the settings
        settingsViewModel.themeColorIndex.observe(this, { index ->
            val currentTheme =
                AVAILABLE_THEME_COLORS.find { it.themeIndex == index }
                    ?: AVAILABLE_THEME_COLORS[DEFAULT_THEME_INDEX]

            binding.themeColorRect.setBackgroundColor(ContextCompat.getColor(this, currentTheme.colorResId))
        })

        setContentView(binding.root)

        binding.fabSave.setOnClickListener {
            settingsViewModel.savedSettings(this@SettingsActivity)
            // refreshing the activity to display the new settings changes
            binding.fabSave.postDelayed({ recreate() }, 500)
        }

        binding.themeColor.setOnClickListener {
            openThemeColorDialog()
        }

        binding.darkModeSwitch.isChecked = settingsViewModel.darkModeState
        binding.darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.changeDarkModeState(isChecked)
        }

        settingsViewModel.settingsChanged.observe(this){
            if (it)
                setResult(RESULT_OK)
            else
                setResult(RESULT_CANCELED)


        }
    }

    /**
     * this function load the saved settings and display them on the screen
     */
    private fun setCurrentSettings() {
        // setting the dark mode state
        settingsViewModel.setCurrentDarkModeState(this)

        // setting the theme
        settingsViewModel.setCurrentTheme(this)
    }

    private fun openThemeColorDialog() {
        val dialogBinding = PickThemeColorLayoutBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(this).setView(dialogBinding.root)
            .create()

        // the action after the color is clicked
        val onColorClicked: (Int) -> Unit = { themeIndex ->
            settingsViewModel.changeThemeIndex(themeIndex)
            dialog.cancel()
        }

        // the color list rv
        dialogBinding.rvColors.adapter =
            ThemeColorAdapter(this@SettingsActivity, AVAILABLE_THEME_COLORS, onColorClicked)
        dialogBinding.rvColors.layoutManager = GridLayoutManager(this, 3)
        dialogBinding.rvColors.setHasFixedSize(true)

        // removing the white background
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }
}