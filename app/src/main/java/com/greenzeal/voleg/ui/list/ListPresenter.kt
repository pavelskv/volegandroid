package com.greenzeal.voleg.ui.list

import com.greenzeal.voleg.api.ApiServiceInterface
import com.greenzeal.voleg.models.Adv
import com.greenzeal.voleg.models.Advs
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*

class ListPresenter : ListContract.Presenter {

    private val subscriptions = CompositeDisposable()
    private val api: ApiServiceInterface = ApiServiceInterface.create()
    private lateinit var view: ListContract.View

    private var currentPage: Int = 0
    private var isLoading: Boolean = false

    override fun subscribe() {

    }

    override fun unsubscribe() {
        subscriptions.clear()
    }

    override fun attach(view: ListContract.View) {
        this.view = view
    }

    override fun onSettingsButtonClick() {
        view.showSettingsFragment()
    }

    override fun onAddingButtonClick() {
        view.showAddingFragment()
    }

    override fun onNewsButtonClick() {
        view.showNewsFragment()
    }

    override fun firstPage() {
        currentPage = 1
        view.clear()
        displayWallpapers()
    }

    override fun nextPage() {
        if (isLoading) return
        currentPage++
        displayWallpapers()
    }



    private fun displayWallpapers() {
        if (isLoading) return
        isLoading = true
        if (currentPage == 1)
            view.showProgress(true)

        val subscribe = api.getAdvList(currentPage)
            .map(Advs::advs)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ adv: ArrayList<Adv>? ->
                isLoading = false
                view.showProgress(false)
                view.loadDataSuccess(adv!!)
            }, { error ->
                view.showProgress(false)
                error.printStackTrace()
                view.showErrorMessage("ERROR")
                isLoading = false
            })

        subscriptions.add(subscribe)
    }
}

