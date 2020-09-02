package com.greenzeal.voleg.di.module

import com.greenzeal.voleg.api.ApiServiceInterface
import com.greenzeal.voleg.ui.adding.AddingContract
import com.greenzeal.voleg.ui.adding.AddingPresenter
import com.greenzeal.voleg.ui.list.ListContract
import com.greenzeal.voleg.ui.list.ListPresenter
import com.greenzeal.voleg.ui.news.NewsContract
import com.greenzeal.voleg.ui.news.NewsPresenter
import com.greenzeal.voleg.ui.settings.SettingsContract
import com.greenzeal.voleg.ui.settings.SettingsPresenter
import dagger.Module
import dagger.Provides

@Module
class FragmentModule {

    @Provides
    fun provideSettingsPresenter(): SettingsContract.Presenter {
        return SettingsPresenter()
    }

    @Provides
    fun provideAddingPresenter(): AddingContract.Presenter {
        return AddingPresenter()
    }

    @Provides
    fun provideListPresenter(): ListContract.Presenter {
        return ListPresenter()
    }

    @Provides
    fun provideNewsPresenter(): NewsContract.Presenter{
        return NewsPresenter()
    }

    @Provides
    fun provideApiService(): ApiServiceInterface {
        return ApiServiceInterface.create()
    }
}