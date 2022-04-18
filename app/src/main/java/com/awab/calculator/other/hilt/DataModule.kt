package com.awab.calculator.other.hilt

import android.content.Context
import androidx.room.Room
import com.awab.calculator.data.local.room.database.HistoryDataBase
import com.awab.calculator.data.local.room.dao.HistoryDao
import com.awab.calculator.data.repository.CalculatorRepository
import com.awab.calculator.data.repository.Repository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object DataModule {

    @Singleton
    @Provides
    fun provideHistoryDataBase(@ApplicationContext context: Context): HistoryDataBase {
        return Room.databaseBuilder(
            context,
            HistoryDataBase::class.java,
            HistoryDataBase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideHistoryDataBaseDao(dataBase: HistoryDataBase): HistoryDao {
        return dataBase.getDao()
    }

    @Singleton
    @Provides
    fun provideRepository(dao: HistoryDao): Repository {
        return CalculatorRepository(dao)
    }
}