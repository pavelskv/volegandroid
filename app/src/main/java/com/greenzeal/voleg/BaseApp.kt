package com.greenzeal.voleg

import android.app.Application
import com.greenzeal.voleg.di.component.ApplicationComponent
import com.greenzeal.voleg.di.component.DaggerApplicationComponent
import com.greenzeal.voleg.di.module.ApplicationModule

class BaseApp: Application() {

    lateinit var component: ApplicationComponent

    override fun onCreate() {
        super.onCreate()

        instance = this
        setup()
    }

    private fun setup() {
        component = DaggerApplicationComponent.builder()
                .applicationModule(ApplicationModule(this)).build()
        component.inject(this)
    }

    fun getApplicationComponent(): ApplicationComponent {
        return component
    }

    companion object {
        lateinit var instance: BaseApp private set
    }
}