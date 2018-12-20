package com.philo.interview.activities

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import com.philo.interview.BuildConfig
import com.philo.interview.DataProviders.FragmentDescriptorData
import com.philo.interview.Logging.NotLoggingTree
import com.philo.interview.R
import com.philo.interview.fragments.StarWarsDirectoryFragment
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_item_list.*
import kotlinx.android.synthetic.main.item_list.*
import timber.log.Timber
import timber.log.Timber.DebugTree

val fragmentNotifier = PublishSubject.create<FragmentDescriptorData>()
/**
 * An activity representing a list of Pings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [StarWarsDirectoryActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class MainActivity : AppCompatActivity() {
    private var compositeDisposable = CompositeDisposable()
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
            addFragmentBasedOnId(FragmentDescriptorData(StarWarsDirectoryFragment.fragmentId))
        }
        else
            startFragmentActivityBasedOnId(FragmentDescriptorData(StarWarsDirectoryActivity.activityId))
        initiateNotificationMonitoring()
    }

    override fun onPause() {
        compositeDisposable.clear()
        super.onPause()
    }

    private fun initiateNotificationMonitoring() {
        compositeDisposable.add(fragmentNotifier.subscribeWith(object : DisposableObserver<FragmentDescriptorData>() {
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
        }))
    }

    private fun addFragmentBasedOnId(payLoad: FragmentDescriptorData) {
        when (payLoad.id) {
            StarWarsDirectoryFragment.fragmentId -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(
                        R.id.fragment_container,
                        StarWarsDirectoryFragment.newInstance(),
                        payLoad.id
                    )
                    .commit()
            }
        }
    }

    private fun startFragmentActivityBasedOnId(payLoad: FragmentDescriptorData) {
        when (payLoad.id) {
            StarWarsDirectoryActivity.activityId -> {
                val intent = Intent(applicationContext, StarWarsDirectoryActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                applicationContext.startActivity(intent)
            }
        }
    }
}
