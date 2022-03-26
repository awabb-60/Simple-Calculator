package com.awab.calculator.other.hilt

import android.content.Context
import androidx.room.Room
import com.awab.calculator.MyApplication
import com.awab.calculator.data.HistoryDataBase
import com.awab.calculator.data.Repository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object DataModule {

    @Singleton
    @Provides
    fun provideMyApplication(@ApplicationContext context:Context):MyApplication{
        return context as MyApplication
    }

    @Singleton
    @Provides
    fun provideHistoryDataBase(application: MyApplication): HistoryDataBase {
        return Room.databaseBuilder(
            application.applicationContext,
            HistoryDataBase::class.java,
            HistoryDataBase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideRepository(dataBase: HistoryDataBase):Repository = Repository(dataBase)

}