package com.awab.calculator.view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.awab.calculator.data.data_models.ThemeModel
import com.awab.calculator.databinding.ActivitySettingsBinding
import com.awab.calculator.databinding.PickThemeColorLayoutBinding
import com.awab.calculator.utils.adapters.ThemeColorAdapter
import com.awab.calculator.viewmodels.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity(),ThemeColorAdapter.ThemeColorListener {

    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var binding: ActivitySettingsBinding

    /**
     * the dialog that will show the available themes for the user to select
     */
    private var colorSelectorDialog:AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]

        // set the current app settings only at the start of the activity
        // then it will get used form the viewModel
        if (savedInstanceState == null)
            saveCurrentSettingsToViewModel()

        setSavedViewSettings()

        setObserves()

        // inflating the layout
        binding = ActivitySettingsBinding.inflate(layoutInflater)

        // now the set the content view
        setContentView(binding.root)

        binding.fabSave.setOnClickListener {
            settingsViewModel.savedSettings(this@SettingsActivity)
            // refreshing the activity to display the new settings changes
            binding.fabSave.postDelayed({ recreate() }, 200)
        }

        binding.themeColor.setOnClickListener {
            openThemeColorDialog()
        }

        binding.darkModeSwitch.isChecked = settingsViewModel.darkModeState
        binding.darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.changeDarkModeState(isChecked)
        }
    }

    /**
     * this function load the saved settings and display them on the screen
     */
    private fun saveCurrentSettingsToViewModel() {
        // setting saved the dark mode state
        settingsViewModel.setCurrentDarkModeState(this)

        // setting saved the theme
        settingsViewModel.setCurrentThemeInfo(this)
    }

    /**
     * this will put the saved view settings not what the user has selected
     */
    private fun setSavedViewSettings(){
        if (settingsViewModel.getSavedDarkModeState(this))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        setTheme(settingsViewModel.getSavedTheme(this).resId)
    }

    private fun setObserves() {
        // updating the theme rect to display the new theme color
        settingsViewModel.theme.observe(this, { theme ->
            binding.themeColorRect.setBackgroundColor(ContextCompat.getColor(this, theme.color))
        })

        settingsViewModel.settingsSaved.observe(this){saved->
            if (saved)
                setResult(RESULT_OK)
            else
                setResult(RESULT_CANCELED)
        }
    }

    private fun openThemeColorDialog() {
        val dialogBinding = PickThemeColorLayoutBinding.inflate(layoutInflater)
        colorSelectorDialog = AlertDialog.Builder(this).setView(dialogBinding.root)
            .create()

        dialogBinding.rvColors.adapter =
            ThemeColorAdapter(this, AVAILABLE_THEME_COLORS, this)
        dialogBinding.rvColors.layoutManager = GridLayoutManager(this, 3)
        dialogBinding.rvColors.setHasFixedSize(true)

        // removing the white background
        colorSelectorDialog?.let {
            it.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        it.show()
        }
    }

    override fun onColorSelected(theme: ThemeModel) {
        settingsViewModel.changeTheme(theme)
        colorSelectorDialog?.cancel()
    }
}