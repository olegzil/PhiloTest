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
import com.philo.interview.DataProviders.StarWarsPerson
import com.philo.interview.R
import com.philo.interview.activities.publisherAdapterToMain
import com.philo.interview.adapters.SimpleItemRecyclerViewAdapter
import com.philo.interview.constants.SWAPI_ROOT
import com.philo.interview.datacontrollers.RequestStarWarsDirectory
import com.philo.interview.datacontrollers.StarWarsDetailGenerator
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
    private val compositeDisposable = CompositeDisposable()
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mProgressBar: ProgressBar
    private var startupData = ItemDetailDescriptor(DIRECTORYDISPLAY, SWAPI_ROOT, "")
    val publisherFragmentToAdapter = PublishSubject.create<ItemDetailDescriptor>()
    lateinit var theAdapter:SimpleItemRecyclerViewAdapter

    val progress: (Boolean) -> Unit = { flag ->
        mProgressBar.run {
            if (flag) {
                visibility = View.VISIBLE
            } else {
                visibility = View.INVISIBLE
            }
        }
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
            theAdapter= SimpleItemRecyclerViewAdapter(publisherAdapterToMain)
            mRecyclerView.adapter = theAdapter
            val divider = DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL)
            divider.setDrawable(ContextCompat.getDrawable(context!!, R.drawable.custom_devider)!!)
            mRecyclerView.addItemDecoration(divider)
        }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dispatchItemDetailHandler()
        savedInstanceState?.run {
            if (!isEmpty && get(StarWarsDirectoryFragment.fragmentId) != null)
                startupData = get(StarWarsDirectoryFragment.fragmentId) as ItemDetailDescriptor
            publisherFragmentToAdapter.onNext(startupData)
        }?: run{
            publisherFragmentToAdapter.onNext(startupData)
        }
    }

    private fun displayDirectory() {
        (fetchJsonDataFromServerItem(SWAPI_ROOT) { jsonData -> populateDirecotry(jsonData) })?.run {
            compositeDisposable.add(
                subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { progress(true) }
                    .doAfterSuccess { progress(false) }
                    .subscribeWith(RequestStarWarsDirectory(mRecyclerView.adapter as SimpleItemRecyclerViewAdapter))
            )
        }
    }

    private fun dispatchItemDetailHandler() {
        compositeDisposable.add(
            publisherFragmentToAdapter
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<ItemDetailDescriptor>() {
                    override fun onComplete() {}

                    override fun onNext(itemDescriptor: ItemDetailDescriptor) {
                        when (itemDescriptor.selector) {
                            STARWARSPERSONS ->
                                (fetchJsonDataFromServerList(itemDescriptor.payload as String) { jsonData ->
                                    populatePersonData(jsonData)
                                })?.run {
                                    subsriber(
                                        progress,
                                        mRecyclerView,
                                        compositeDisposable,
                                        STARWARSPERSONDETAIL,
                                        { container -> container.name},
                                        this
                                    )
                                }
                            STARWARSPERSONDETAIL -> {
                                val detail = itemDescriptor.payload as StarWarsPerson
                                val buffer = StringBuffer()
                                buffer.append("Name: ${detail.name}\n" +
                                        "Birth year: ${detail.birthYear}\n" +
                                        "Eye color: ${detail.eyeColor}\n" +
                                        "Gender: ${detail.gender}\n" +
                                        "Hair color: ${detail.hairColor}\n" +
                                        "Height: ${detail.height}\n" +
                                        "Mass: ${detail.mass}\n" +
                                        "Skin color: ${detail.skinColor}\n")
                                StarWarsDetailGenerator(mRecyclerView.adapter as SimpleItemRecyclerViewAdapter, buffer.toString())
                            }
                            STARWARSPLANETS -> (fetchJsonDataFromServerList(
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
                                    STARWARSPLANETDETAIL,
                                    { container -> container.name },
                                    this
                                )
                            }
                            STARWARSSTARSHIPS -> (fetchJsonDataFromServerList(
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
                                    STARWARSSTARSHIPSDETAIL,
                                    { container -> container.name },
                                    this
                                )
                            }
                            STARWARSVEHICLES -> (fetchJsonDataFromServerList(
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
                                    STARWARSVEHICLESDETAIL,
                                    { container -> container.name },
                                    this
                                )
                            }
                            STRWARSSPECIES -> (fetchJsonDataFromServerList(
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
                                    STARWARSSPECIESDETAIL,
                                    { container -> container.name },
                                    this
                                )
                            }
                            DIRECTORYDISPLAY -> displayDirectory()
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
        const val STRWARSSPECIES: String = "STRWARSSPECIES"
        const val STARWARSSPECIESDETAIL: String = "STARWARSSPECIESDETAIL"
        const val STARWARSVEHICLES: String = "STARWARSVEHICLES"
        const val STARWARSVEHICLESDETAIL: String = "STARWARSVEHICLESDETAIL"
        const val STARWARSPERSONS: String = "STARWARSPERSONS"
        const val STARWARSPERSONDETAIL: String = "STARWARSPERSONDETAIL"
        const val STARWARSPLANETS: String = "STARWARSPLANETS"
        const val STARWARSPLANETDETAIL: String = "STARWARSPLANETDETAIL"
        const val STARWARSSTARSHIPS: String = "STARWARSSTARSHIPS"
        const val STARWARSSTARSHIPSDETAIL: String = "STARWARSSTARSHIPSDETAIL"
        const val STARWARSDIRECTORYITEM: String = "STARWARSDIRECTORYITEM"
        const val STARWARSFILM: String = "STARWARSFILM"
        const val DIRECTORYDISPLAY: String = "DIRECTORYDISPLAY"
        const val DETAILITEM:String="DETAILITEM"
        const val DONOTDISPLAY:String ="DONOTDISPLAY"
        fun newInstance(startupData:ItemDetailDescriptor) : StarWarsDirectoryFragment{
            val retVal = StarWarsDirectoryFragment()
            retVal.startupData = startupData
            return retVal
        }
    }
}
