package com.greenzeal.voleg.ui.list

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdRequest
import com.greenzeal.voleg.R
import com.greenzeal.voleg.databinding.FragmentListBinding
import com.greenzeal.voleg.di.component.DaggerFragmentComponent
import com.greenzeal.voleg.di.module.FragmentModule
import com.greenzeal.voleg.models.Adv
import com.greenzeal.voleg.ui.adapter.AdvAdapter
import com.greenzeal.voleg.ui.adapter.AdvAdapter.OnItemClickListener
import com.greenzeal.voleg.views.CallDialogFragment
import javax.inject.Inject

class ListFragment : Fragment(), ListContract.View {

    @Inject
    lateinit var presenter: ListContract.Presenter

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private lateinit var callback: ListContract.Callback

    private var layoutManager: LinearLayoutManager? = null

    private var adapter: AdvAdapter? = null

    fun newInstance(): ListFragment {
        return ListFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ListContract.Callback)
            callback = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectDependency()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        val view = binding.root

        val adRequest = AdRequest.Builder().build()
        binding.bottomAdView.loadAd(adRequest)

        binding.settingsBtn.setOnClickListener { presenter.onSettingsButtonClick() }
        binding.newsBtn.setOnClickListener { presenter.onNewsButtonClick() }
        binding.addBtn.setOnClickListener { presenter.onAddingButtonClick() }

        binding.swipeRefreshLayout.setColorSchemeColors(resources.getColor(R.color.colorAccent))

        binding.btnRetry.setOnClickListener {presenter.firstPage()}
        binding.swipeRefreshLayout.setOnRefreshListener {
            presenter.firstPage()
        }

        adapter = AdvAdapter(Glide.with(this), object : OnItemClickListener {
            override fun providerClick(provider: String, numbers: MutableList<String>?) {
                val callDialogFragment = CallDialogFragment(provider, numbers)
                callDialogFragment.show(fragmentManager!!, "CallDialog")
            }
        })
        binding.recyclerView.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(context)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.setItemViewCacheSize(10)
        adapter?.setHasStableIds(true)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addOnScrollListener(onScrollListener)

        presenter.attach(this)
        presenter.subscribe()
        initView()

        return view
    }

    private val onScrollListener: RecyclerView.OnScrollListener =
        object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val visibleItemCount: Int = layoutManager!!.childCount
                val totalItemCount: Int = layoutManager!!.itemCount
                val firstVisibleItemPosition: Int = layoutManager!!.findFirstVisibleItemPosition()
                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
                    presenter.nextPage()
                }
            }
        }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.unsubscribe()
    }

    override fun showProgress(show: Boolean) {
        if (show) {
            binding.errorLayout.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    override fun showErrorMessage(error: String) {
        binding.errorLayout.visibility = View.VISIBLE
    }

    override fun loadDataSuccess(list: ArrayList<Adv>) {
        adapter?.addAll(list)
    }

    override fun showSettingsFragment() {
        callback.onSettingsClick()
    }

    override fun showAddingFragment() {
        callback.onAddingClick()
    }

    override fun showNewsFragment() {
        callback.onNewsClick()
    }

    override fun clear() {
        adapter?.clear()
    }

    private fun injectDependency() {
        val listComponent = DaggerFragmentComponent.builder()
            .fragmentModule(FragmentModule())
            .build()

        listComponent.inject(this)
    }

    private fun initView() {
        presenter.firstPage()
    }

    fun onUpdate() {
        presenter.firstPage()
    }

    companion object {
        const val TAG: String = "ListFragment"
    }
}