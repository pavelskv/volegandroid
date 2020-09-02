package com.greenzeal.voleg.di.component

import com.greenzeal.voleg.ui.adding.AddingFragment
import com.greenzeal.voleg.ui.list.ListFragment
import com.greenzeal.voleg.ui.settings.SettingsFragment
import com.greenzeal.voleg.di.module.FragmentModule
import com.greenzeal.voleg.ui.news.NewsFragment
import dagger.Component

@Component(modules = [FragmentModule::class])
interface FragmentComponent {

    fun inject(settingsFragment: SettingsFragment)
    fun inject(listFragment: ListFragment)
    fun inject(addingFragment: AddingFragment)
    fun inject(newsFragment: NewsFragment)

}