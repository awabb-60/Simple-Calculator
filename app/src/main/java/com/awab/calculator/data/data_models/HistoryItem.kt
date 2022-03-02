package com.awab.calculator.data.data_models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * the history item model class
 */
@Entity(tableName = "history_items")
data class HistoryItem(val equation:String, val answer:String, @PrimaryKey(autoGenerate = true) val id:Int = 0)