package com.awab.calculator.view

import com.awab.calculator.R
import com.awab.calculator.data.data_models.ThemeColor

const val SETTINGS_REQUEST_CODE = 999
const val SETTINGS_CHANGED = "SETTINGS_CHANGED"

const val SETTINGS_SHARED_PREFERENCES = "SETTINGS_SHARED_PREFERENCES"
const val CURRENT_THEME_INDEX = "CURRENT_THEME_INDEX"
const val CURRENT_DARK_MODE_STATE = "CURRENT_DARK_MODE_STATE"

const val DEFAULT_THEME_INDEX = 0
const val DEFAULT_DARK_MODE_STATE = false

val AVAILABLE_THEME_COLORS = listOf(
    ThemeColor(0, R.color.theme_color_0),
    ThemeColor(1, R.color.theme_color_1),
    ThemeColor(2, R.color.theme_color_2),
    ThemeColor(3, R.color.theme_color_3),
    ThemeColor(4, R.color.theme_color_4),
    ThemeColor(5, R.color.theme_color_5),
    ThemeColor(6, R.color.theme_color_6),
    ThemeColor(7, R.color.theme_color_7),
    ThemeColor(8, R.color.theme_color_8),
)
