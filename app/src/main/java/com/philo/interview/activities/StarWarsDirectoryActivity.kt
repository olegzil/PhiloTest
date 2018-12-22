package com.philo.interview.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.philo.interview.DataProviders.ItemDetailDescriptor
import com.philo.interview.R
import com.philo.interview.fragments.StarWarsDirectoryFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_item_detail.*
import timber.log.Timber

/**
 * An activity representing a single Item detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a [MainActivity].
 */
val publisherAdapterToMain = PublishSubject.create<ItemDetailDescriptor>()

class StarWarsDirectoryActivity : AppCompatActivity() {
    val compositeDisposable = CompositeDisposable()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail)
        setSupportActionBar(detail_toolbar)
        initiateNotificationMonitoring()

        // Show the Up button in the action bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        savedInstanceState?.let {
            Timber.i("saved sate present")
        } ?: let {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            val fragment = StarWarsDirectoryFragment().apply {
                arguments = Bundle().apply {
                    putString(
                        StarWarsDirectoryFragment.fragmentId,
                        intent.getStringExtra(StarWarsDirectoryFragment.DIRECTORYDISPLAY)
                    )
                }
            }
            supportFragmentManager.beginTransaction()
                .add(R.id.item_detail_container, fragment)
                .commit()
        }
    }

    override fun onPause() {
        super.onPause()
        compositeDisposable.clear()
    }

    private fun initiateNotificationMonitoring() {
        compositeDisposable.add(publisherAdapterToMain
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<ItemDetailDescriptor>() {
            override fun onComplete() {
                // Do nothing
            }

            override fun onNext(payLoad: ItemDetailDescriptor) {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.item_detail_container, StarWarsDirectoryFragment.newInstance(payLoad))
                    .addToBackStack(null)
                    .commit()
            }

            override fun onError(e: Throwable) {
                Timber.e(e)
            }
        }))
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            android.R.id.home -> {
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    companion object {
        const val activityId = "9d589c1e-d809-4f9e-9917-7a0de294708d"
    }
}
