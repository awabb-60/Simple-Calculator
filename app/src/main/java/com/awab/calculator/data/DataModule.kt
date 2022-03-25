package com.awab.calculator.data

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.awab.calculator.MyApplication
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
    fun provideHistoryDataBase(application: MyApplication):HistoryDateBase{
        return Room.databaseBuilder(
            application.applicationContext,
            HistoryDateBase::class.java,
            "history_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideRepository(dataBase:HistoryDateBase):Repository{
        return Repository(dataBase)
    }


}