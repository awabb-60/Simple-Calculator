package com.awab.calculator.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.awab.calculator.data.data_models.HistoryItem

@Database(entities = [HistoryItem::class], version = 1, exportSchema = false)
abstract class HistoryDateBase : RoomDatabase() {

    /**
     * get the DAO of this room database
     * @return DAO of type HistoryItemsDao
     */
    abstract fun getDao(): HistoryItemsDao

    companion object {

        /**
         * a singleton instance of this database
        */
        @Volatile
        private var INSTANCE: HistoryDateBase? = null

        /**
         *  returns the singleton instance of this database
         *  @return room database of type HistoryDateBase
         */
        fun getInstance(context: Context): HistoryDateBase {
            synchronized(this) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        HistoryDateBase::class.java,
                        "history_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                }
                return INSTANCE as HistoryDateBase
            }
        }

    }
}