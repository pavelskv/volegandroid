package com.greenzeal.voleg.di.module

import android.app.Application
import com.greenzeal.voleg.BaseApp
import com.greenzeal.voleg.di.scope.PerApplication
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationModule(private val baseApp: BaseApp) {

    @Provides
    @Singleton
    @PerApplication
    fun provideApplication(): Application {
        return baseApp
    }
}