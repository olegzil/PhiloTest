package com.philo.interview.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.philo.interview.R
import kotlinx.android.synthetic.main.activity_item_detail.*
import kotlinx.android.synthetic.main.item_detail.view.*

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a [MainActivity]
 * in two-pane mode (on tablets) or a [ItemDetailActivity]
 * on handsets.
 */
class ItemDetailFragment : Fragment() {

    private var item: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let { bundle ->
            if (bundle.containsKey(fragmentId)) {
                // Load the dummy content specified by the fragment
                // arguments. In a real-world scenario, use a Loader
                // to load content from a content provider.
                bundle.getString(ItemDetailFragment.fragmentId)?.let { title ->
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

        // Show the dummy content as text in a TextView.
        item?.let {
            rootView.item_detail.text = "replace this with real data"
        }

        return rootView
    }

    companion object {
        const val fragmentId = "bd20fdb5-b0be-4c50-909e-99b6e6b290fe"
        fun newInstance(data: String): ItemDetailFragment {
            return ItemDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(fragmentId, data)
                }
            }
        }
    }
}
