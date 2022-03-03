package com.awab.calculator.view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.awab.calculator.R
import com.awab.calculator.data.data_models.ThemeColor
import com.awab.calculator.databinding.ActivitySettingsBinding
import com.awab.calculator.databinding.PickThemeColorLayoutBinding
import com.awab.calculator.other.ThemeColorAdapter

class SettingsActivity : AppCompatActivity() {

    private val darkModeStateKey = "darkModeStateKey"
    private val themeColorIndexKey = "themeColorIndex"

    private lateinit var binding:ActivitySettingsBinding

    private val settings = mutableMapOf<String, Any>(
        darkModeStateKey to true,
        themeColorIndexKey to 0,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fabSave.setOnClickListener {
            setResult(RESULT_OK)
            saveSettings()
        }

        binding.tvThemeColor.setOnClickListener {
            val dialogBinding = PickThemeColorLayoutBinding.inflate(layoutInflater)
            val dialog = AlertDialog.Builder(this).setView(dialogBinding.root)
                .create()

            val availableThemeColors = listOf(
                ThemeColor(0, ContextCompat.getColor(this@SettingsActivity, R.color.theme_color_0)),
                ThemeColor(1, ContextCompat.getColor(this@SettingsActivity, R.color.theme_color_1)),
                ThemeColor(2, ContextCompat.getColor(this@SettingsActivity, R.color.theme_color_2)),
                ThemeColor(3, ContextCompat.getColor(this@SettingsActivity, R.color.theme_color_3)),
                ThemeColor(4, ContextCompat.getColor(this@SettingsActivity, R.color.theme_color_4)),
                ThemeColor(5, ContextCompat.getColor(this@SettingsActivity, R.color.theme_color_5)),
                ThemeColor(6, ContextCompat.getColor(this@SettingsActivity, R.color.theme_color_6)),
                ThemeColor(7, ContextCompat.getColor(this@SettingsActivity, R.color.theme_color_7)),
                ThemeColor(8, ContextCompat.getColor(this@SettingsActivity, R.color.theme_color_8)),
            )

            // the action after the color is clicked
            val colorOnClick: (Int) -> Unit = { themeIndex ->
                settings.remove(themeColorIndexKey)
                settings[themeColorIndexKey] = themeIndex
                binding.tvThemeColor.setBackgroundResource(availableThemeColors[themeIndex].color)
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

        binding.darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            settings.remove(darkModeStateKey)
            settings[darkModeStateKey] = isChecked
        }
    }

    private fun saveSettings() {
    }
}