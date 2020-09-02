package com.greenzeal.voleg.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.ads.MobileAds
import com.greenzeal.voleg.R
import com.greenzeal.voleg.di.component.DaggerActivityComponent
import com.greenzeal.voleg.di.module.ActivityModule
import com.greenzeal.voleg.ui.adding.AddingFragment
import com.greenzeal.voleg.ui.list.ListContract
import com.greenzeal.voleg.ui.list.ListFragment
import com.greenzeal.voleg.ui.news.NewsFragment
import com.greenzeal.voleg.ui.settings.SettingsFragment
import com.greenzeal.voleg.views.*
import javax.inject.Inject


class MainActivity : AppCompatActivity(), MainContract.View, ListContract.Callback,
    CallDialogFragment.CallListener, ImagePickDialogFragment.PickListener,
    NumbersDialogFragment.AddNumberListener,
    ProvidersDialogFragment.ProviderListener, ConfirmDialogFragment.ConfirmListener,
AcceptParcelsDialog.AcceptListener{

    @Inject
    lateinit var presenter: MainContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MobileAds.initialize(this) {}

        injectDependency()
        presenter.attach(this)
    }

    override fun showListFragment() {
        supportFragmentManager.beginTransaction()
            .disallowAddToBackStack()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .replace(R.id.frame, ListFragment().newInstance(), ListFragment.TAG)
            .commit()
    }

    override fun onBackPressed() {
        val fragmentManager = supportFragmentManager
        val settingsFragment = fragmentManager.findFragmentByTag(SettingsFragment.TAG)
        val addingFragment = fragmentManager.findFragmentByTag(AddingFragment.TAG)

        if (settingsFragment == null || addingFragment == null) {
            super.onBackPressed()
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    private fun injectDependency() {
        val activityComponent = DaggerActivityComponent.builder()
            .activityModule(ActivityModule(this))
            .build()

        activityComponent.inject(this)
    }

    override fun onSettingsClick() {
        if (supportFragmentManager.findFragmentByTag(SettingsFragment.TAG) == null) {
            supportFragmentManager.beginTransaction()
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.frame, SettingsFragment().newInstance(), SettingsFragment.TAG)
                .commit()
        }
    }

    override fun onNavigationClick() {
        onBackPressed()
    }

    override fun onAddingClick() {
        if (supportFragmentManager.findFragmentByTag(AddingFragment.TAG) == null) {
            supportFragmentManager.beginTransaction()
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.frame, AddingFragment().newInstance(), AddingFragment.TAG)
                .commit()
        }
    }

    override fun onNewsClick() {
        if (supportFragmentManager.findFragmentByTag(NewsFragment.TAG) == null) {
            supportFragmentManager.beginTransaction()
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.frame, NewsFragment().newInstance(), NewsFragment.TAG)
                .commit()
        }
    }

    override fun onCall(provider: String, number: String) {
        try {
            when (provider) {
                "mobile", "landline" -> {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null))
                    startActivity(intent)
                }
                "whatsapp" -> {
                    val url = "https://api.whatsapp.com/send?phone=$number"
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(url)
                    startActivity(i)
                }
                "viber" -> {
                    val url = "viber://contact?number=${number.replace("+", "")}"
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(url)
                    startActivity(i)
                }
                "telegram" -> {
                    val url = "tg://msg?to=$number"
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(url)
                    startActivity(i)
                }
            }
        } catch (ex: Exception) {
            Toast.makeText(this, getString(R.string.provider_is_not_available, provider), Toast.LENGTH_LONG).show()
        }

    }

    override fun pickButtonClicked(which: Int) {
        val fragmentManager = supportFragmentManager
        val addingFragment = fragmentManager.findFragmentByTag(AddingFragment.TAG)

        if (addingFragment != null && addingFragment is AddingFragment) {
            when (which) {
                0 -> addingFragment.dispatchTakePictureIntent()
                1 -> addingFragment.chooseGallery()
            }
        }
    }

    override fun addNumberClick(number: String?, providers: MutableList<String>) {
        val fragmentManager = supportFragmentManager
        val addingFragment = fragmentManager.findFragmentByTag(AddingFragment.TAG)

        if (addingFragment != null && addingFragment is AddingFragment) {
            addingFragment.addNumber(number, providers)
        }
    }

    override fun updateProvider(
        number: String?,
        selectedItems: MutableList<String>,
        adapterPosition: Int
    ) {
        val fragmentManager = supportFragmentManager
        val addingFragment = fragmentManager.findFragmentByTag(AddingFragment.TAG)

        if (addingFragment != null && addingFragment is AddingFragment) {
            addingFragment.updateProvider(number, selectedItems, adapterPosition)
        }
    }

    override fun onConfirm(isUpdate: Boolean) {
        if(isUpdate){
            onBackPressed()
            val fragmentManager = supportFragmentManager
            val listFragment = fragmentManager.findFragmentByTag(ListFragment.TAG)

            if (listFragment != null && listFragment is ListFragment) {
                listFragment.onUpdate()
            }
        }
        else onBackPressed()
    }

    override fun onAccept(accept: Int) {
        val fragmentManager = supportFragmentManager
        val addingFragment = fragmentManager.findFragmentByTag(AddingFragment.TAG)

        if (addingFragment != null && addingFragment is AddingFragment) {
            addingFragment.setAcceptParcels(accept)
        }
    }
}