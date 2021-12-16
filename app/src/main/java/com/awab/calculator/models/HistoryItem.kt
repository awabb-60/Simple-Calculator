package com.awab.calculator.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history_items")
data class HistoryItem(val equation:String, val answer:String, @PrimaryKey(autoGenerate = true) val id:Int = 0)