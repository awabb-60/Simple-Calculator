package com.awab.calculator.viewmodels

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.awab.calculator.R
import com.awab.calculator.view.CURRENT_DARK_MODE_STATE
import com.awab.calculator.view.CURRENT_THEME_INDEX
import com.awab.calculator.view.DEFAULT_THEME_INDEX
import com.awab.calculator.view.SETTINGS_SHARED_PREFERENCES

class SettingsViewModel : ViewModel() {

    var darkModeState: Boolean = false

    private val _themeColorIndex = MutableLiveData(0)
    private val _themeRes = MutableLiveData(0)

    private val _settingsChanged = MutableLiveData(false)

    val settingsChanged: LiveData<Boolean>
        get() = _settingsChanged

    val themeColorIndex: LiveData<Int>
        get() = _themeColorIndex

    val themeRes: LiveData<Int>
        get() = _themeRes

    fun savedSettings(context: Context) {
        _settingsChanged.value = true
        val spEditor =
            context.getSharedPreferences(SETTINGS_SHARED_PREFERENCES, AppCompatActivity.MODE_PRIVATE).edit()
        spEditor.putBoolean(CURRENT_DARK_MODE_STATE, darkModeState)
        spEditor.putInt(CURRENT_THEME_INDEX, _themeColorIndex.value!!)
        spEditor.apply()
        setCurrentTheme(context)
    }

    fun setCurrentDarkModeState(context: Context) {
        val sp = context.getSharedPreferences(SETTINGS_SHARED_PREFERENCES, AppCompatActivity.MODE_PRIVATE)

        // returning the theme corresponding to the saved theme index
        darkModeState = sp.getBoolean(CURRENT_DARK_MODE_STATE, false)
        return
    }

    fun setCurrentTheme(context: Context) {
        val sp = context.getSharedPreferences(SETTINGS_SHARED_PREFERENCES, AppCompatActivity.MODE_PRIVATE)
        // returning the theme corresponding to the saved theme index
        when (sp.getInt(CURRENT_THEME_INDEX, DEFAULT_THEME_INDEX)) {
            1 -> R.style.theme1
            2 -> R.style.theme2
            3 -> R.style.theme3
            4 -> R.style.theme4
            5 -> R.style.theme5
            6 -> R.style.theme6
            7 -> R.style.theme7
            8 -> R.style.theme8
            else -> R.style.Theme_Calculator
        }.let { themeIndex ->
            _themeColorIndex.value = sp.getInt(CURRENT_THEME_INDEX, DEFAULT_THEME_INDEX)
            _themeRes.value =             themeIndex
        }
    }

    fun changeThemeIndex(themeIndex: Int) {
        _themeColorIndex.value = themeIndex
    }

    fun changeDarkModeState(checked: Boolean) {
        darkModeState = checked
    }
}