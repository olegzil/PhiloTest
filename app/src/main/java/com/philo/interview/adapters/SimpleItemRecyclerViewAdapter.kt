package com.philo.interview.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.philo.interview.DataProviders.FragmentDescriptorData
import com.philo.interview.R
import com.philo.interview.fragments.StarWarsDirectoryFragment
import com.philo.interview.interfaces.RecyclerAdapterInterface
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.item_list_content.view.*

class SimpleItemRecyclerViewAdapter(
    private val publishSubject: PublishSubject<FragmentDescriptorData>
) :
    RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.StarWarsNameHolder>(), RecyclerAdapterInterface<String> {
    override fun update() {
        notifyDataSetChanged()
    }

    private val values =  mutableListOf<String>()

    override fun getItemDetailByPosition(index: Int): String {
        return values[index]
    }

    override fun getAllItems(): List<String> = values
    private val onClickListener: View.OnClickListener

    /*
    Setup a click listener to send data to the fragment that knows how do display it.
    The data is the name selected by the user, i.e. the name of the StarWars character.
    * */
    init {
        onClickListener = View.OnClickListener { v ->
            publishSubject.onNext(
                FragmentDescriptorData(
                    StarWarsDirectoryFragment.fragmentId
                )
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StarWarsNameHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list_content, parent, false)
        return StarWarsNameHolder(view)
    }

    override fun onBindViewHolder(holder: StarWarsNameHolder, position: Int) {
        holder.bind(values[position], position)
    }

    override fun getItemCount() = values.size

    inner class StarWarsNameHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(name: String, position: Int) {
            with(itemView) {
                tag = name
                id_text.text = position.toString()
                content.text = name
                setOnClickListener(onClickListener)
            }
        }
    }

    companion object {
        val maxAdapterSize = 1000000
        val maxPageSize = 5
    }
}
