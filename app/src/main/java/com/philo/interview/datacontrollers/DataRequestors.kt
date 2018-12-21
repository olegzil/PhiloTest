package com.philo.interview.datacontrollers

import com.philo.interview.DataProviders.ItemDetailDescriptor
import com.philo.interview.DataProviders.StarWarsDirectoryItem
import com.philo.interview.adapters.SimpleItemRecyclerViewAdapter
import com.philo.interview.fragments.StarWarsDirectoryFragment.Companion.DETAILITEM
import com.philo.interview.fragments.StarWarsDirectoryFragment.Companion.STARWARSFILM
import com.philo.interview.fragments.StarWarsDirectoryFragment.Companion.STARWARSPERSONS
import com.philo.interview.fragments.StarWarsDirectoryFragment.Companion.STARWARSPLANETS
import com.philo.interview.fragments.StarWarsDirectoryFragment.Companion.STARWARSSTARSHIPS
import com.philo.interview.fragments.StarWarsDirectoryFragment.Companion.STARWARSVEHICLES
import com.philo.interview.fragments.StarWarsDirectoryFragment.Companion.STRWARSSPECIES
import com.philo.interview.utilities.printLog
import io.reactivex.observers.DisposableSingleObserver

interface BaserRequestor
class RequestStarWarsDataList<R>(
    private val selector: String,
    private val adapter: SimpleItemRecyclerViewAdapter,
    private val dataExtractor: (R) -> String
) : DisposableSingleObserver<List<R>>(), BaserRequestor {
    override fun onSuccess(directoryList: List<R>) {
        val returnValues = ArrayList<ItemDetailDescriptor>()
        directoryList.forEach {
            returnValues.add(
                ItemDetailDescriptor(
                    selector,
                    dataExtractor(it), it as Any
                )
            )
        }
        adapter.update(returnValues)
    }

    override fun onError(e: Throwable) {
        printLog("failed to retrieve string")
    }
}

class StarWarsDetailGenerator(private val adapter: SimpleItemRecyclerViewAdapter, data: String) {
    val tokens = data.split("\n")
    val entries = ArrayList<ItemDetailDescriptor>()

    init {

        tokens.forEach { item ->
            if (item.isNotEmpty())
                entries.add(
                    ItemDetailDescriptor(
                        DETAILITEM,
                        item,
                        ""
                    )
                )
        }
        adapter.update(entries)
    }
}

class RequestStarWarsDirectory(
    private val theAdapter: SimpleItemRecyclerViewAdapter
) : DisposableSingleObserver<StarWarsDirectoryItem>(), BaserRequestor {
    override fun onSuccess(directory: StarWarsDirectoryItem) {
        val entries = ArrayList<ItemDetailDescriptor>()

        var tokens = directory.filmsUrl.split('/')
        entries.add(
            ItemDetailDescriptor(
                STARWARSFILM,
                tokens[tokens.size - 2],
                directory.filmsUrl
            )
        )
        tokens = directory.peopleUrl.split('/')
        entries.add(
            ItemDetailDescriptor(
                STARWARSPERSONS,
                tokens[tokens.size - 2],
                directory.peopleUrl
            )
        )
        tokens = directory.planetsUrl.split('/')
        entries.add(
            ItemDetailDescriptor(
                STARWARSPLANETS,
                tokens[tokens.size - 2],
                directory.planetsUrl
            )
        )
        tokens = directory.speciesUrl.split('/')
        entries.add(
            ItemDetailDescriptor(
                STRWARSSPECIES,
                tokens[tokens.size - 2],
                directory.speciesUrl
            )
        )
        tokens = directory.starshipsUrl.split('/')
        entries.add(
            ItemDetailDescriptor(
                STARWARSSTARSHIPS,
                tokens[tokens.size - 2],
                directory.starshipsUrl
            )
        )
        tokens = directory.vehiclesUrl.split('/')
        entries.add(
            ItemDetailDescriptor(
                STARWARSVEHICLES,
                tokens[tokens.size - 2],
                directory.vehiclesUrl
            )
        )
        theAdapter.update(entries)
    }

    override fun onError(e: Throwable) {
        printLog("failed to retrieve string")
    }
}