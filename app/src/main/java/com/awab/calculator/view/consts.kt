package com.awab.calculator.view

import com.awab.calculator.R
import com.awab.calculator.data.data_models.ThemeModel

const val SETTINGS_REQUEST_CODE = 999

const val SETTINGS_CHANGED = "SETTINGS_CHANGED"

const val SETTINGS_SHARED_PREFERENCES = "SETTINGS_SHARED_PREFERENCES"
const val CURRENT_THEME_NUMBER = "CURRENT_THEME_INDEX"
const val CURRENT_DARK_MODE_STATE = "CURRENT_DARK_MODE_STATE"

val AVAILABLE_THEME_COLORS = listOf(
    ThemeModel(0, R.style.Theme_Calculator, R.color.theme_color_0),
    ThemeModel(1, R.style.theme1, R.color.theme_color_1),
    ThemeModel(2, R.style.theme2, R.color.theme_color_2),
    ThemeModel(3, R.style.theme3, R.color.theme_color_3),
    ThemeModel(4, R.style.theme4, R.color.theme_color_4),
    ThemeModel(5, R.style.theme5, R.color.theme_color_5),
    ThemeModel(6, R.style.theme6, R.color.theme_color_6),
    ThemeModel(7, R.style.theme7, R.color.theme_color_7),
    ThemeModel(8, R.style.theme8, R.color.theme_color_8)
)

val DEFAULT_THEME_NUMBER = AVAILABLE_THEME_COLORS[0].themeNumber
const val DEFAULT_DARK_MODE_STATE = false
