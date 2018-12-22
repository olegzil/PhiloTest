package com.philo.interview.fragments

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.philo.interview.DataProviders.ItemDetailDescriptor
import com.philo.interview.DataProviders.StarWarsEpisode
import com.philo.interview.DataProviders.StarWarsPerson
import com.philo.interview.DataProviders.StarWarsStarships
import com.philo.interview.R
import com.philo.interview.Server.NetworkServiceInitializer
import com.philo.interview.Server.RetrofitNetworkService
import com.philo.interview.activities.publisherAdapterToMain
import com.philo.interview.adapters.SimpleItemRecyclerViewAdapter
import com.philo.interview.constants.SWAPI_ROOT
import com.philo.interview.datacontrollers.RequestStarWarsDirectory
import com.philo.interview.datacontrollers.StarWarsDetailGenerator
import com.philo.interview.lambdas.subsriber
import com.philo.interview.utilities.*
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import org.json.JSONObject
import timber.log.Timber

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
    lateinit var theAdapter: SimpleItemRecyclerViewAdapter
    lateinit var searchButton: FloatingActionButton

    val progress: (Boolean) -> Unit = { flag ->
        mProgressBar.run {
            if (flag) {
                visibility = View.VISIBLE
            } else {
                visibility = View.INVISIBLE
            }
        }
    }
    val search:(Boolean) -> Unit = {flag ->
        if (flag)
            searchButton.visibility = View.VISIBLE
        else
            searchButton.visibility = View.INVISIBLE
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

    private fun singlePageSearch(searchString: String) {
        if (searchString.isEmpty()) {
            Toast.makeText(context, "Search string is empty", Toast.LENGTH_SHORT).show()
            return
        }
        val base = "https://swapi.co/api/people/"
        RetrofitNetworkService(NetworkServiceInitializer(base)).getApi()?.run {
            compositeDisposable.add(
                search(searchString)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribeWith(object : DisposableSingleObserver<String>() {
                        override fun onSuccess(data: String) {
                            Toast.makeText(context, parseHeader(data, searchString), Toast.LENGTH_LONG).show()
                        }

                        override fun onError(e: Throwable) {
                            Timber.e(e)
                        }
                    })
            )
        }
    }

    private fun searchAndDisplay(searchString: String) {
        val root = "https://swapi.co/api/people/"
        progress(true)
        search(false)
        RetrofitNetworkService(NetworkServiceInitializer(root))
            .getApi()?.search(searchString)?.run {
                compositeDisposable.add(
                    flatMap { jsonData ->
                        val result = parseHeader(jsonData, searchString)
                        if ( result == null)
                            RetrofitNetworkService(NetworkServiceInitializer(root)).getApi()
                                ?.fetchData(" ")
                                ?.flatMap {rawJson->

                                    Single.just<Pair<String, Boolean>>(Pair(rawJson, false))
                                }
                        else
                            Single.just(Pair<String, Boolean>(result, true))
                    }
                        .flatMap { pair ->
                            val jsonString = pair.first
                            val selector = pair.second
                            if (selector)
                                Single.just(jsonString)
                            else {
                                var nextPage: String = JSONObject(jsonString).optString("next", "*")
                                var result = parseHeader(jsonString, searchString)
                                while (nextPage != "null" && result == null) {
                                    RetrofitNetworkService(NetworkServiceInitializer(nextPage)).getApi()
                                        ?.fetchData(nextPage)
                                        ?.blockingGet()
                                        ?.let { receivedString ->
                                            nextPage = JSONObject(receivedString).optString("next", null)
                                            result = parseHeader(receivedString, searchString)
                                            if (result != null) {
                                                nextPage = "null"
                                                Single.just(result)
                                            } else if (nextPage != "null") {
                                                nextPage = nextPage.split("&dummy")[0]
                                            } else {
                                                result = "Could not find $searchString"
                                                Single.just(result)
                                            }
                                        } ?: run {
                                        nextPage = "*"
                                    }
                                }
                                Single.just(result)
                            }
                        }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribeWith(object : DisposableSingleObserver<String>() {
                            override fun onSuccess(data: String) {
                                progress(false)
                                search(true)
                                Toast.makeText(context, data, Toast.LENGTH_LONG).show()
                            }

                            override fun onError(e: Throwable) {
                                progress(false)
                                search(true)
                                Timber.e(e)
                            }
                        })
                )
            }
    }

    private fun initializeFragment(view: View) {
        searchButton = view.findViewById<FloatingActionButton>(R.id.fab)
        val inputField = view.findViewById<EditText>(R.id.input_text)
        searchButton?.setOnClickListener {
            if (inputField.visibility == View.GONE)
                inputField?.visibility = View.VISIBLE
            else {
                inputField?.onEditorAction(EditorInfo.IME_ACTION_DONE);
                inputField?.visibility = View.GONE

                searchAndDisplay(inputField?.text.toString())
            }

        }
        view.let { recyclerView ->
            mRecyclerView = recyclerView.findViewById(R.id.recycler_view)
            mProgressBar = recyclerView.findViewById(R.id.progressbar)
            val mLayoutManager = LinearLayoutManager(context)
            mRecyclerView.layoutManager = mLayoutManager
            mRecyclerView.itemAnimator = DefaultItemAnimator()
            theAdapter = SimpleItemRecyclerViewAdapter(publisherAdapterToMain)
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
        } ?: run {
            publisherFragmentToAdapter.onNext(startupData)
        }
    }

    private fun displayDirectory() {
        (fetchJsonDataFromServerItem(SWAPI_ROOT) { jsonData -> populateDirectory(jsonData) })?.run {
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
                                        { container -> container.name },
                                        this
                                    )
                                }
                            STARWARSFILM ->
                                (fetchJsonDataFromServerList(itemDescriptor.payload as String) { jsonData ->
                                    populateEpisodeData(jsonData)
                                })?.run {
                                    subsriber(
                                        progress,
                                        mRecyclerView,
                                        compositeDisposable,
                                        STARWARSFILMSDETAIL,
                                        { container -> container.name },
                                        this
                                    )
                                }
                            STARWARSFILMSDETAIL ->{
                                val detail = itemDescriptor.payload as StarWarsEpisode
                                val buffer = StringBuffer()
                                buffer.append(
                                    "Name: ${detail.name}\n" +
                                            "Directory: ${detail.director}\n" +
                                            "Episode ID: ${detail.episodeId}\n" +
                                            "Title: ${detail.name}\n" +
                                            "Crawl: ${detail.openingCrawl}\n" +
                                            "Producer: ${detail.producer}\n" +
                                            "Release date: ${detail.releaseDate}\n"
                                )
                                StarWarsDetailGenerator(
                                    mRecyclerView.adapter as SimpleItemRecyclerViewAdapter,
                                    buffer.toString()
                                )
                            }

                            STARWARSPERSONDETAIL -> {
                                val detail = itemDescriptor.payload as StarWarsPerson
                                val buffer = StringBuffer()
                                buffer.append(
                                    "Name: ${detail.name}\n" +
                                            "Birth year: ${detail.birthYear}\n" +
                                            "Eye color: ${detail.eyeColor}\n" +
                                            "Gender: ${detail.gender}\n" +
                                            "Hair color: ${detail.hairColor}\n" +
                                            "Height: ${detail.height}\n" +
                                            "Mass: ${detail.mass}\n" +
                                            "Skin color: ${detail.skinColor}\n"
                                )
                                StarWarsDetailGenerator(
                                    mRecyclerView.adapter as SimpleItemRecyclerViewAdapter,
                                    buffer.toString()
                                )
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
                                    DONOTDISPLAY,
                                    { container -> container.name },
                                    this
                                )
                            }
                            STARWARSSTARSHIPSDETAIL -> {
                                val detail = itemDescriptor.payload as StarWarsStarships
                                val buffer = StringBuffer()
                                buffer.append(
                                    "Name: ${detail.name}\n" +
                                            "MGLT: ${detail.MGLT}\n" +
                                            "Air speed: ${detail.airSpeed}\n" +
                                            "Cargo capacity: ${detail.cargoCapacity}\n" +
                                            "Consumables: ${detail.consumables}\n" +
                                            "Cost: ${detail.cost}\n" +
                                            "Crew: ${detail.crew}\n" +
                                            "Hyperdrive Rating: ${detail.hyperdriveRating}\n" +
                                            "Length: ${detail.length}\n" +
                                            "Manufacturer: ${detail.manufacturer}\n" +
                                            "Model: ${detail.model}\n" +
                                            "Class: ${detail.sclass}\n" +
                                            "Passengers: ${detail.passengers}\n"
                                )
                                StarWarsDetailGenerator(
                                    mRecyclerView.adapter as SimpleItemRecyclerViewAdapter,
                                    buffer.toString()
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
                                    DONOTDISPLAY,
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
                                    DONOTDISPLAY,
                                    { container -> container.name },
                                    this
                                )
                            }
                            DIRECTORYDISPLAY -> {
                                displayDirectory()
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
        const val STARWARSFILMSDETAIL: String = "STARWARSFILMS"
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
        const val DETAILITEM: String = "DETAILITEM"
        const val DONOTDISPLAY: String = "DONOTDISPLAY"
        fun newInstance(startupData: ItemDetailDescriptor): StarWarsDirectoryFragment {
            val retVal = StarWarsDirectoryFragment()
            retVal.startupData = startupData
            return retVal
        }
    }
}
