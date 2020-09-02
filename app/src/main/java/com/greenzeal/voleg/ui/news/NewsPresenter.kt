package com.greenzeal.voleg.ui.news

import com.greenzeal.voleg.api.ApiServiceInterface
import com.greenzeal.voleg.models.News
import com.greenzeal.voleg.models.NewsList
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*

class NewsPresenter : NewsContract.Presenter {

    private val subscriptions = CompositeDisposable()
    private val api: ApiServiceInterface = ApiServiceInterface.create()
    private lateinit var view: NewsContract.View

    private var currentPage: Int = 0
    private var isLoading: Boolean = false


    override fun firstPage() {
        currentPage = 1
        view.clear()
        displayNews()
    }

    override fun nextPage() {
        if (isLoading) return
        currentPage++
        displayNews()
    }

    override fun subscribe() {
    }

    override fun unsubscribe() {
        subscriptions.clear()
    }

    override fun attach(view: NewsContract.View) {
        this.view = view
    }

    private fun displayNews() {
        if (isLoading) return
        isLoading = true
        if (currentPage == 1)
            view.showProgress(true)

        val subscribe = api.getNewsList(currentPage)
            .map(NewsList::news)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ news: ArrayList<News>? ->
                isLoading = false
                view.showProgress(false)
                view.loadDataSuccess(news!!)
            }, { error ->
                view.showProgress(false)
                error.printStackTrace()
                view.showErrorMessage("ERROR")
                isLoading = false
            })

        subscriptions.add(subscribe)
    }
}