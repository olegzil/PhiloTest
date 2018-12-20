package com.philo.interview.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.philo.interview.DataProviders.ItemDetailDescriptor
import com.philo.interview.R
import com.philo.interview.interfaces.RecyclerAdapterInterface
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.item_list_content.view.*

class SimpleItemRecyclerViewAdapter(
    private val publishSubject: PublishSubject<ItemDetailDescriptor>
) :
    RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.StarWarsNameHolder>(), RecyclerAdapterInterface<ItemDetailDescriptor> {
    private val values =  mutableListOf<ItemDetailDescriptor>()

    override fun update(newItems: List<ItemDetailDescriptor>) {
        values.clear()
        values.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun getItemDetailByPosition(index: Int): ItemDetailDescriptor {
        return values[index]
    }

    override fun getAllItems(): List<ItemDetailDescriptor> = values
    private val onClickListener: View.OnClickListener

    /*
    Setup a click listener to send data to the fragment that knows how do display it.
    The data is the name selected by the user, i.e. the name of the StarWars character.
    * */
    init {
        onClickListener = View.OnClickListener { theView ->
            val data = (theView.tag as ItemDetailDescriptor)
            val payload = theView.tag as ItemDetailDescriptor
            publishSubject.onNext(payload)
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
        fun bind(item:ItemDetailDescriptor, position: Int) {
            with(itemView) {
                tag = item
                id_text.text = position.toString()
                content.text = item.data
                setOnClickListener(onClickListener)
            }
        }
    }
}
