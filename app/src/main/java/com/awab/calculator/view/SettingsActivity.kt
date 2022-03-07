package com.awab.calculator.view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.awab.calculator.databinding.ActivitySettingsBinding
import com.awab.calculator.databinding.PickThemeColorLayoutBinding
import com.awab.calculator.other.ThemeColorAdapter
import com.awab.calculator.viewmodels.SettingsViewModel

class SettingsActivity : AppCompatActivity() {

    private var darkModeState = false
    private var themeColorIndex = 0

    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var binding: ActivitySettingsBinding
    private val TAG = "SettingsActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]

        settingsViewModel.settingsChanged.observe(this, { settingsChanged ->
            setResult(
                if (settingsChanged)
                    RESULT_OK
                else RESULT_CANCELED
            )
        })



        if (savedInstanceState == null)
            setCurrentSettings()

        Log.d(TAG, "onCreate: 1")
        AppCompatDelegate.setDefaultNightMode(if(settingsViewModel.darkModeState)
            AppCompatDelegate.MODE_NIGHT_YES
        else
            AppCompatDelegate.MODE_NIGHT_NO)
        setTheme(settingsViewModel.themeRes.value!!)

        // inflating the layout 2
        binding = ActivitySettingsBinding.inflate(layoutInflater)

        // this has to before inflating the layout 1
        // only at the start of the activity
        // after that the view will get the saved settings from the viewModel

        // display the settings
        settingsViewModel.themeColorIndex.observe(this, { index ->
            val currentTheme =
                AVAILABLE_THEME_COLORS.find { it.themeIndex == index }
                    ?: AVAILABLE_THEME_COLORS[DEFAULT_THEME_INDEX]

            binding.themeColorRect.setBackgroundColor(ContextCompat.getColor(this, currentTheme.colorResId))
            Log.d(TAG, "onCreate: 4")
        })

        // 3
        setContentView(binding.root)
        Log.d(TAG, "onCreate: 2")

        binding.fabSave.setOnClickListener {
            settingsViewModel.savedSettings(this@SettingsActivity)
            Toast.makeText(this@SettingsActivity, "settings saved", Toast.LENGTH_SHORT).show()
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

//        binding.themeColorRect.postDelayed({
//            binding.themeColorRect.setBackgroundColor(Color.RED)
//        },1000)
//        binding.themeColorRect.postDelayed({
//            binding.themeColorRect.setBackgroundColor(Color.BLUE)
//        },2000)
//        binding.themeColorRect.postDelayed({
//            binding.themeColorRect.setBackgroundColor(Color.GREEN)
//        },3000)
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