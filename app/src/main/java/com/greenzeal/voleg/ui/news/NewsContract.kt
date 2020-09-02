package com.greenzeal.voleg.ui.news

import com.greenzeal.voleg.models.News
import com.greenzeal.voleg.ui.base.BaseContract

class NewsContract {



    interface View: BaseContract.View{
        fun showProgress(show: Boolean)
        fun showErrorMessage(error: String)
        fun loadDataSuccess(list: ArrayList<News>)

        fun clear()
    }

    interface Presenter: BaseContract.Presenter<View> {
        fun firstPage()
        fun nextPage()
    }

}