package com.greenzeal.voleg.di.module

import android.app.Activity
import com.greenzeal.voleg.ui.main.MainContract
import com.greenzeal.voleg.ui.main.MainPresenter
import dagger.Module
import dagger.Provides

@Module
class ActivityModule(private var activity: Activity) {

    @Provides
    fun provideActivity(): Activity {
        return activity
    }

    @Provides
    fun providePresenter(): MainContract.Presenter {
        return MainPresenter()
    }

}