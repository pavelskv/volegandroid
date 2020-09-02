package com.greenzeal.voleg.ui.adding

import android.net.Uri
import com.google.gson.Gson
import com.greenzeal.voleg.BaseApp
import com.greenzeal.voleg.api.ApiServiceInterface
import com.greenzeal.voleg.models.Adv
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.*

class AddingPresenter : AddingContract.Presenter {

    private val subscriptions = CompositeDisposable()
    private lateinit var view: AddingContract.View
    private val api: ApiServiceInterface = ApiServiceInterface.create()

    override fun submitAdv(adv: Adv, file: File?) {
        view.showProgress(true)

        val jsonData = Gson().toJson(adv)
        val boundary = generateBoundary()

        val subscribe: Disposable

        if (file != null) {
            subscribe =
                api.submitAdv(boundary, MultipartBody.Part.createFormData("json", jsonData),
                    MultipartBody.Part.createFormData(
                        "image",
                        file.name,
                        file
                            .asRequestBody(
                                BaseApp.instance.contentResolver.getType(
                                                            Uri.fromFile(file)
                                                        )?.toMediaTypeOrNull()
                            )
                    )
                )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        view.showProgress(false)
                        view.loadAdvSuccess()
                    }, { error ->
                        view.showProgress(false)
                        view.showErrorMessage(error.message)
                    })

        } else
            subscribe =
                api.submitAdv(boundary, MultipartBody.Part.createFormData("json", jsonData))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        view.showProgress(false)
                        view.loadAdvSuccess()
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

    override fun attach(view: AddingContract.View) {
        this.view = view
    }

    private fun generateBoundary(): String {
        return "Boundary-" + UUID.randomUUID()
    }
}