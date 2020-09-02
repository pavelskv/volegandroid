package com.greenzeal.voleg.di.component

import com.greenzeal.voleg.di.module.ActivityModule
import com.greenzeal.voleg.ui.main.MainActivity
import dagger.Component

@Component(modules = [ActivityModule::class])
interface ActivityComponent {

    fun inject(mainActivity: MainActivity)

}