package com.greenzeal.voleg.ui.main

import com.greenzeal.voleg.ui.base.BaseContract

class MainContract {

    interface View: BaseContract.View {
        fun showListFragment()
    }

    interface Presenter: BaseContract.Presenter<View>
}