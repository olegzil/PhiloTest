package com.philo.interview.lambdas

import android.support.v7.widget.RecyclerView
import com.philo.interview.adapters.SimpleItemRecyclerViewAdapter
import com.philo.interview.datacontrollers.RequestStarWarsDataList
import com.philo.interview.fragments.StarWarsDirectoryFragment
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

fun <R> subsriber(progress:(Boolean)->Unit, recyclerView:RecyclerView, disposables: CompositeDisposable, selector: StarWarsDirectoryFragment.DataDetailIdentifiers, field: (R) -> String, parent: Single<List<R>>): Boolean {
    return disposables.add(
        parent.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { progress(true) }
            .doAfterSuccess { progress(false) }
            .subscribeWith(
                RequestStarWarsDataList(
                    selector,
                    recyclerView.adapter as SimpleItemRecyclerViewAdapter,
                    field
                )
            )
    )

}

