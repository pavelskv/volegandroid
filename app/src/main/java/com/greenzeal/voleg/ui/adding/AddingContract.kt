package com.greenzeal.voleg.ui.adding

import com.greenzeal.voleg.models.Adv
import com.greenzeal.voleg.ui.base.BaseContract
import java.io.File

class AddingContract {

    interface View: BaseContract.View {
        fun addItem(file: File?)
        fun showProgress(b: Boolean)
        fun showErrorMessage(message: String?)
        fun loadAdvSuccess()
    }

    interface Presenter: BaseContract.Presenter<View> {
        fun submitAdv(adv: Adv, file: File?)
    }
}
