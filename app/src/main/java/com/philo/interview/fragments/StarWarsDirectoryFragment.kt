package com.philo.interview.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.philo.interview.DataProviders.ItemDetailDescriptor
import com.philo.interview.R
import com.philo.interview.adapters.SimpleItemRecyclerViewAdapter
import com.philo.interview.datacontrollers.RequestStarWarsDirectory
import com.philo.interview.lambdas.subsriber
import com.philo.interview.utilities.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a [MainActivity]
 * in two-pane mode (on tablets) or a [ItemDetailActivity]
 * on handsets.
 */
class StarWarsDirectoryFragment : Fragment() {
    private val recyclerDispatcher = PublishSubject.create<ItemDetailDescriptor>()
    private val compositeDisposable = CompositeDisposable()
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mProgressBar: ProgressBar

    enum class DataDetailIdentifiers {
        STRWARSSPECIES,
        STARWARSSPECIESDETAIL,
        STARWARSVEHICLES,
        STARWARSVEHICLESDETAIL,
        STARWARSPERSONS,
        STARWARSPERSONDETAIL,
        STARWARSPLANETS,
        STARWARSPLANETDETAIL,
        STARWARSSTARSHIPS,
        STARWARSSTARSHIPSDETAIL,
        STARWARSDIRECTORYITEM,
        STARWARSFILM
    }

    override fun onPause() {
        super.onPause()
        compositeDisposable.clear()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.item_list, container, false)
        initializeFragment(view)
        return view
    }

    private fun initializeFragment(view: View) {
        view.let { recyclerView ->
            mRecyclerView = recyclerView.findViewById(R.id.recycler_view)
            mProgressBar = recyclerView.findViewById(R.id.progressbar)
            val mLayoutManager = LinearLayoutManager(context)
            mRecyclerView.layoutManager = mLayoutManager
            mRecyclerView.itemAnimator = DefaultItemAnimator()
            mRecyclerView.adapter = SimpleItemRecyclerViewAdapter(recyclerDispatcher)
            val divider = DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL)
            divider.setDrawable(ContextCompat.getDrawable(context!!, R.drawable.custom_devider)!!)
            mRecyclerView.addItemDecoration(divider)
            dispatchItemDetailHandler()
        }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (fetchJsonDataFromServerItem("https://swapi.co/api/") { jsonData -> populateDirecotry(jsonData) })?.run {
            compositeDisposable.add(
                subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(RequestStarWarsDirectory(mRecyclerView.adapter as SimpleItemRecyclerViewAdapter))
            )
        }

    }

    private fun dispatchItemDetailHandler() {
        val progress: (Boolean) -> Unit = { flag ->
            mProgressBar.run {
                    if (flag) {
                        visibility = View.VISIBLE
                    }
                    else {
                        visibility = View.INVISIBLE
                    }
                }
        }
        compositeDisposable.add(
            recyclerDispatcher
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<ItemDetailDescriptor>() {
                    override fun onComplete() {}

                    override fun onNext(itemDescriptor: ItemDetailDescriptor) {
                        val adapter = mRecyclerView.adapter as SimpleItemRecyclerViewAdapter
                        when (itemDescriptor.selector) {
                            DataDetailIdentifiers.STARWARSPERSONS ->
                                (fetchJsonDataFromServerList(itemDescriptor.payload as String) { jsonData ->
                                    populatePersonData(jsonData)
                                })?.run {
                                    subsriber(
                                        progress,
                                        mRecyclerView,
                                        compositeDisposable,
                                        itemDescriptor.selector,
                                        { container -> container.name },
                                        this
                                    )
                                }
                            DataDetailIdentifiers.STARWARSPLANETS -> (fetchJsonDataFromServerList(
                                itemDescriptor.payload as String
                            ) { jsonData ->
                                populatePlanetList(
                                    jsonData
                                )
                            })?.run {
                                subsriber(
                                    progress,
                                    mRecyclerView,
                                    compositeDisposable,
                                    itemDescriptor.selector,
                                    { container -> container.name },
                                    this
                                )
                            }
                            DataDetailIdentifiers.STARWARSSTARSHIPS -> (fetchJsonDataFromServerList(
                                itemDescriptor.payload as String
                            ) { jsonData ->
                                populateStarshipData(
                                    jsonData
                                )
                            })?.run {
                                subsriber(
                                    progress,
                                    mRecyclerView,
                                    compositeDisposable,
                                    itemDescriptor.selector,
                                    { container -> container.name },
                                    this
                                )
                            }
                            DataDetailIdentifiers.STARWARSVEHICLES -> (fetchJsonDataFromServerList(
                                itemDescriptor.payload as String
                            ) { jsonData ->
                                populateVehicleList(
                                    jsonData
                                )
                            })?.run {
                                subsriber(
                                    progress,
                                    mRecyclerView,
                                    compositeDisposable,
                                    itemDescriptor.selector,
                                    { container -> container.name },
                                    this
                                )
                            }
                            DataDetailIdentifiers.STRWARSSPECIES -> (fetchJsonDataFromServerList(
                                itemDescriptor.payload as String
                            ) { jsonData ->
                                populateSpiciesList(
                                    jsonData
                                )
                            })?.run {
                                subsriber(
                                    progress,
                                    mRecyclerView,
                                    compositeDisposable,
                                    itemDescriptor.selector,
                                    { container -> container.name },
                                    this
                                )
                            }
                            else -> printLog("item not handled")
                        }
                    }

                    override fun onError(e: Throwable) {
                    }

                })
        )
    }

    companion object {
        const val fragmentId = "bd20fdb5-b0be-4c50-909e-99b6e6b290fe"
        fun newInstance() = StarWarsDirectoryFragment()
    }
}
