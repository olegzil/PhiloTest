package com.philo.interview.activities

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import com.philo.interview.BuildConfig
import com.philo.interview.DataProviders.FragmentDescriptorData
import com.philo.interview.Logging.NotLoggingTree
import com.philo.interview.R
import com.philo.interview.adapters.SimpleItemRecyclerViewAdapter
import com.philo.interview.fragments.ItemDetailFragment
import io.reactivex.disposables.Disposables
import io.reactivex.observers.DisposableObserver
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_item_list.*
import kotlinx.android.synthetic.main.item_list.*
import timber.log.Timber
import timber.log.Timber.DebugTree


/**
 * An activity representing a list of Pings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [ItemDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class MainActivity : AppCompatActivity() {
    private val fragmentNotifier = PublishSubject.create<FragmentDescriptorData>()
    private var disposable = Disposables.disposed()
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var twoPane: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (BuildConfig.DEBUG)
            Timber.plant(DebugTree().apply {
                Timber.tag("=-=-=-=-=-=")
            })
        else
            Timber.plant(NotLoggingTree())

        setContentView(R.layout.activity_item_list)

        setSupportActionBar(toolbar)
        toolbar.title = title

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        if (item_detail_container != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            twoPane = true
        }
        disposable = fragmentNotifier.subscribeWith(object : DisposableObserver<FragmentDescriptorData>() {
            override fun onComplete() {
                // Do nothing
            }

            override fun onNext(payLoad: FragmentDescriptorData) =
                if (item_detail_container != null)
                    addFragmentBasedOnId(payLoad)
                else {
                    startFragmentActivityBasedOnId(payLoad)
                }


            override fun onError(e: Throwable) {
                Timber.e(e)
            }
        })
        setupRecyclerView(item_list)
    }

    private fun addFragmentBasedOnId(payLoad: FragmentDescriptorData) {
        when (payLoad.id) {
            ItemDetailFragment.fragmentId -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(
                        R.id.item_detail_container,
                        ItemDetailFragment.newInstance(payLoad.fragmentExtraData),
                        payLoad.id
                    )
                    .commit()
            }
        }
    }

    private fun startFragmentActivityBasedOnId(payLoad: FragmentDescriptorData) {
        when (payLoad.id) {
            ItemDetailFragment.fragmentId -> {
                applicationContext.startActivity(Intent(applicationContext, ItemDetailActivity::class.java).apply {
                    putExtra(ItemDetailActivity.activityId, payLoad.fragmentExtraData)
                })
            }
        }
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = SimpleItemRecyclerViewAdapter(fragmentNotifier)
    }
}
