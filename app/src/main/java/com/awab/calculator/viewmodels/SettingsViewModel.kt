package com.awab.calculator.viewmodels

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.awab.calculator.data.data_models.ThemeModel
import com.awab.calculator.view.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel
@Inject constructor() : ViewModel() {
    var darkModeState: Boolean = false

    // the key number for the theme
//    private val _themeNumber = MutableLiveData(0)

    // the theme resources id
    private val _theme:MutableLiveData<ThemeModel> = MutableLiveData(AVAILABLE_THEME_COLORS[DEFAULT_THEME_NUMBER])


    val theme: LiveData<ThemeModel>
        get() = _theme


    private val _settingsSaved = MutableLiveData(false)

    val settingsSaved: LiveData<Boolean>
        get() = _settingsSaved

//    val themeColorIndex: LiveData<Int>
//        get() = _themeNumber

    fun savedSettings(context: Context) {
        _settingsSaved.value = true

        val spEditor =
            context.getSharedPreferences(SETTINGS_SHARED_PREFERENCES, AppCompatActivity.MODE_PRIVATE).edit()

        spEditor.putInt(CURRENT_THEME_NUMBER, theme.value?.themeNumber?: DEFAULT_THEME_NUMBER)
        spEditor.putBoolean(CURRENT_DARK_MODE_STATE, darkModeState)
        spEditor.apply()
        setCurrentThemeInfo(context)
    }

    fun setCurrentDarkModeState(context: Context) {
        // set the dark mode state
        darkModeState = getSavedDarkModeState(context)
    }

    fun setCurrentThemeInfo(context: Context):ThemeModel {

        // getting the saved theme info
        val currentTheme:ThemeModel = getSavedTheme(context)

        _theme.value = currentTheme
        return currentTheme
    }

    fun getSavedDarkModeState(context: Context): Boolean {
        val sp = context.getSharedPreferences(SETTINGS_SHARED_PREFERENCES, AppCompatActivity.MODE_PRIVATE)
        return sp.getBoolean(CURRENT_DARK_MODE_STATE, DEFAULT_DARK_MODE_STATE)
    }

    fun getSavedTheme(context: Context):ThemeModel{
        val sp = context.getSharedPreferences(SETTINGS_SHARED_PREFERENCES, AppCompatActivity.MODE_PRIVATE)

        // getting the save theme number or the default option
        val currentThemeNumber = sp.getInt(CURRENT_THEME_NUMBER, DEFAULT_THEME_NUMBER)

        // return the saved theme
        return AVAILABLE_THEME_COLORS.find { it.themeNumber == currentThemeNumber }
            ?: AVAILABLE_THEME_COLORS[DEFAULT_THEME_NUMBER]
    }

    fun changeTheme(theme: ThemeModel) {
        _theme.value = theme
    }

    fun changeDarkModeState(checked: Boolean) {
        darkModeState = checked
    }
}