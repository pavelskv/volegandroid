package com.greenzeal.voleg.ui.list

import com.greenzeal.voleg.models.Adv
import com.greenzeal.voleg.ui.base.BaseContract

class ListContract {

    interface Callback {
        fun onSettingsClick()
        fun onNavigationClick()
        fun onAddingClick()
        fun onNewsClick()
    }

    interface View: BaseContract.View {
        fun showProgress(show: Boolean)
        fun showErrorMessage(error: String)
        fun loadDataSuccess(list: ArrayList<Adv>)

        fun showSettingsFragment()
        fun showAddingFragment()
        fun showNewsFragment()

        fun clear()
    }

    interface Presenter: BaseContract.Presenter<View> {
        fun onSettingsButtonClick()
        fun onAddingButtonClick()
        fun onNewsButtonClick()

        fun firstPage()
        fun nextPage()
    }
}