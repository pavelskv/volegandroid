package com.greenzeal.voleg.ui.settings

import com.google.gson.Gson
import com.greenzeal.voleg.api.ApiServiceInterface
import com.greenzeal.voleg.models.Feedback
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import java.util.*


class SettingsPresenter : SettingsContract.Presenter {

    private val subscriptions = CompositeDisposable()
    private val api: ApiServiceInterface = ApiServiceInterface.create()

    private lateinit var view: SettingsContract.View

    override fun submitFeedback(feedback: Feedback) {
        view.showProgress(true)

        val jsonData = Gson().toJson(feedback)
        val boundary = generateBoundary()

        val subscribe =
            api.submitFeedback(boundary, MultipartBody.Part.createFormData("json", jsonData))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.showProgress(false)
                    view.loadFeedbackSuccess()
                }, { error ->
                    view.showProgress(false)
                    view.showErrorMessage(error.message)
                })

        subscriptions.add(subscribe)
    }


    override fun subscribe() {

    }

    override fun unsubscribe() {
        subscriptions.clear()
    }

    override fun attach(view: SettingsContract.View) {
        this.view = view
    }

    private fun generateBoundary(): String {
        return "Boundary-" + UUID.randomUUID()
    }
}