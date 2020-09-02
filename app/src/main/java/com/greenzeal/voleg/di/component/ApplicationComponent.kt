package com.greenzeal.voleg.di.component

import com.greenzeal.voleg.BaseApp
import com.greenzeal.voleg.di.module.ApplicationModule
import dagger.Component

@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {

    fun inject(application: BaseApp)

}