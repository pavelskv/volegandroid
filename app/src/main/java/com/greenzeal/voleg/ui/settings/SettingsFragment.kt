package com.greenzeal.voleg.ui.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.greenzeal.voleg.R
import com.greenzeal.voleg.databinding.FragmentSettignsBinding
import com.greenzeal.voleg.di.component.DaggerFragmentComponent
import com.greenzeal.voleg.di.module.FragmentModule
import com.greenzeal.voleg.models.Feedback
import com.greenzeal.voleg.ui.list.ListContract
import com.greenzeal.voleg.util.Auth
import com.greenzeal.voleg.util.Utils
import com.greenzeal.voleg.views.ConfirmDialogFragment
import com.greenzeal.voleg.views.LoadingDialogFragment
import javax.inject.Inject

class SettingsFragment : Fragment(), SettingsContract.View {

    @Inject
    lateinit var presenter: SettingsContract.Presenter

    private var _binding: FragmentSettignsBinding? = null
    private val binding get() = _binding!!
    private var callback: ListContract.Callback? = null
    private var loadingDialogFragment: LoadingDialogFragment? = null

    fun newInstance(): SettingsFragment {
        return SettingsFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectDependency()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ListContract.Callback)
            callback = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettignsBinding.inflate(inflater, container, false)
        val view = binding.root

        val toolbar = binding.toolbar
        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            Utils.hideKeyboard(binding.feedbackTextInputLayout)
            callback!!.onNavigationClick() }

        binding.submitBtn.setOnClickListener { submitFeedback() }

        loadingDialogFragment = LoadingDialogFragment(R.layout.loading_dialog)

        return view
    }

    private fun submitFeedback() {
        if (binding.feedbackEditText.text.isNullOrBlank()) {
            binding.feedbackEditText.error = getString(R.string.no_text_in_feedback)
            binding.feedbackEditText.requestFocus()
            return
        }

        val email =
            if (!binding.emailEditText.text.isNullOrBlank()) binding.emailEditText.text.toString() else ""
        val text =
            if (!binding.feedbackEditText.text.isNullOrBlank()) binding.feedbackEditText.text.toString() else "NO TEXT"

        presenter.submitFeedback(Feedback(email, text, Auth.userId()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.attach(this)
        initView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.unsubscribe()
        _binding = null
    }

    override fun showProgress(show: Boolean) {
        if (show) {
            loadingDialogFragment!!.show(fragmentManager!!, "LoadingDialog")
        } else {
            loadingDialogFragment!!.dismiss()
        }
    }

    override fun loadFeedbackSuccess() {
        Utils.hideKeyboard(binding.feedbackTextInputLayout)
        ConfirmDialogFragment(getString(R.string.thanks_for_feedback), false).show(fragmentManager!!, "ConfirmDialog")
    }

    override fun showErrorMessage(message: String?) {
        Toast.makeText(context, message ?: "Error", Toast.LENGTH_LONG).show()
    }

    private fun injectDependency() {
        val settingsComponent = DaggerFragmentComponent.builder()
            .fragmentModule(FragmentModule())
            .build()

        settingsComponent.inject(this)
    }

    private fun initView() {
    }

    companion object {
        const val TAG: String = "SettingsFragment"
    }
}