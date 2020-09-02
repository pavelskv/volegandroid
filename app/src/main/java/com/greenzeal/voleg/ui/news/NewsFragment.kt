package com.greenzeal.voleg.ui.news

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdRequest
import com.greenzeal.voleg.R
import com.greenzeal.voleg.databinding.FragmentNewsBinding
import com.greenzeal.voleg.di.component.DaggerFragmentComponent
import com.greenzeal.voleg.di.module.FragmentModule
import com.greenzeal.voleg.models.News
import com.greenzeal.voleg.ui.adapter.NewsAdapter
import com.greenzeal.voleg.ui.list.ListContract
import com.greenzeal.voleg.views.WebViewDialog
import javax.inject.Inject

class NewsFragment : Fragment(), NewsContract.View {

    @Inject
    lateinit var presenter: NewsContract.Presenter

    private lateinit var callback: ListContract.Callback

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!

    private var layoutManager: LinearLayoutManager? = null

    private var adapter: NewsAdapter? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        val view = binding.root

        val toolbar = binding.toolbar
        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { callback.onNavigationClick() }

        val adRequest = AdRequest.Builder().build()
        binding.bottomAdView.loadAd(adRequest)

        binding.swipeRefreshLayout.setColorSchemeColors(resources.getColor(R.color.colorAccent))

        binding.btnRetry.setOnClickListener {presenter.firstPage()}
        binding.swipeRefreshLayout.setOnRefreshListener {
            presenter.firstPage()
        }

        adapter = NewsAdapter(Glide.with(this), object : NewsAdapter.OnItemClickListener {
            override fun onNewsClick(url: String?) {
                WebViewDialog.display(fragmentManager, url)
            }
        })

        binding.recyclerView.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(context)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addOnScrollListener(onScrollListener)

        presenter.attach(this)
        presenter.subscribe()
        initView()

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ListContract.Callback)
            callback = context
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

    fun newInstance(): NewsFragment {
        return NewsFragment()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.unsubscribe()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectDependency()
    }

    private fun injectDependency() {
        val newsComponent = DaggerFragmentComponent.builder()
            .fragmentModule(FragmentModule())
            .build()

        newsComponent.inject(this)
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

    override fun loadDataSuccess(list: ArrayList<News>) {
        adapter?.addAll(list)
    }

    override fun clear() {
        adapter?.clear()
    }

    private fun initView() {
        presenter.firstPage()
    }

    fun onUpdate() {
        presenter.firstPage()
    }

    companion object {
        const val TAG: String = "NewsFragment"
    }
}