package com.greenzeal.voleg.ui.settings

import com.greenzeal.voleg.models.Feedback
import com.greenzeal.voleg.ui.base.BaseContract
class SettingsContract {

    interface View : BaseContract.View {
        fun showProgress(show: Boolean)
        fun loadFeedbackSuccess()
        fun showErrorMessage(message: String?)
    }

    interface Presenter : BaseContract.Presenter<View> {
        fun submitFeedback(feedback: Feedback)
    }
}