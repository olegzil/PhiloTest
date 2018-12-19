package com.philo.interview.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.philo.interview.DataProviders.StarWarsPerson
import com.philo.interview.R
import com.philo.interview.Server.NetworkServiceInitializer
import com.philo.interview.Server.RetrofitNetworkService
import com.philo.interview.activities.fragmentNotifier
import com.philo.interview.utilities.populateDirecotry
import com.philo.interview.utilities.populatePersonData
import com.philo.interview.utilities.printLog
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_item_detail.*
import kotlinx.android.synthetic.main.item_detail.view.*
/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a [MainActivity]
 * in two-pane mode (on tablets) or a [ItemDetailActivity]
 * on handsets.
 */
class StarWarsDirectoryFragment : Fragment() {

    private var item: String? = null
    private val server = RetrofitNetworkService(NetworkServiceInitializer("https://swapi.co/api/"))

    private val compositeDisposable = CompositeDisposable()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentNotifier
        arguments?.let { bundle ->
            if (bundle.containsKey(fragmentId)) {
                // Load the dummy content specified by the fragment
                // arguments. In a real-world scenario, use a Loader
                // to load content from a content provider.
                bundle.getString(StarWarsDirectoryFragment.fragmentId)?.let { title ->
                    item = title
                    activity?.toolbar_layout?.title = item
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.item_detail, container, false)
        requestDirectoryDataFromServer()?.run {
            subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<List<StarWarsPerson>>() {
                    override fun onSuccess(characterList: List<StarWarsPerson>) {
                        item?.let {
                            rootView.item_detail.text = getString(R.string.title_directory)
                        }
                    }
                    override fun onError(e: Throwable) {
                        printLog("failed to retrieve string")
                    }
                })
        }
        return rootView
    }

    private fun requestDirectoryDataFromServer(): Single<List<StarWarsPerson>>? {
        return server.getApi()?.fetchData(" ")?.run {
            flatMap { jsonData ->
                val directory = populateDirecotry(jsonData)
                if (directory != null)
                    Single.just(directory)
                else
                    Single.never()
            }
                .flatMap { starwarsDirectory ->
                    server.getApi()?.fetchData(starwarsDirectory.peopleUrl)
                }
                .flatMap { jsonCharacters ->
                    val characters = populatePersonData(jsonCharacters)
                    if (characters != null)
                        Single.just(characters)
                    else
                        Single.never()
                }
        }
    }

    companion object {
        const val fragmentId = "bd20fdb5-b0be-4c50-909e-99b6e6b290fe"
        fun newInstance() = StarWarsDirectoryFragment()
    }
}
