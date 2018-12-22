package com.philo.interview.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.philo.interview.BuildConfig
import com.philo.interview.DataProviders.FragmentDescriptorData
import com.philo.interview.DataProviders.ItemDetailDescriptor
import com.philo.interview.Logging.NotLoggingTree
import com.philo.interview.R
import com.philo.interview.fragments.StarWarsDirectoryFragment
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_item_list.*
import kotlinx.android.synthetic.main.item_list.*
import timber.log.Timber
import timber.log.Timber.DebugTree

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
        if (item_detail_container != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            addFragmentBasedOnId(ItemDetailDescriptor(StarWarsDirectoryFragment.DIRECTORYDISPLAY, "", ""))
        }
        else
            startFragmentActivityBasedOnId(FragmentDescriptorData(StarWarsDirectoryActivity.activityId))
    }

    override fun onPause() {
        compositeDisposable.clear()
        super.onPause()
    }

    private fun addFragmentBasedOnId(payLoad: ItemDetailDescriptor) {
                supportFragmentManager
                    .beginTransaction()
                    .replace(
                        R.id.fragment_container,
                        StarWarsDirectoryFragment.newInstance(payLoad),
                        StarWarsDirectoryFragment.fragmentId
                    )
                    .commit()
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
