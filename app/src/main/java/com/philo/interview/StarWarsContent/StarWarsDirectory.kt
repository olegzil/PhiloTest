package com.philo.interview.StarWarsContent

import com.philo.interview.DataProviders.StarWarsDirectoryItem
import java.util.*

class StarWarsDirectory {

    /**
     * An array of sample (dummy) items.
     */
    private val DIRECTORY_ITEMS: MutableList<StarWarsDirectoryItem> = ArrayList()

    /**
     * A map of sample (dummy) items, by ID.
     */
    private val DIRECTORY_ITEM_MAP: MutableMap<String, StarWarsDirectoryItem> = HashMap()

    private val COUNT = 25

    init {

        // Add some sample items.
        for (i in 1..COUNT) {
            addItem(createDirectoryEntry(i))
        }
    }

    private fun addItem(item: StarWarsDirectoryItem) {
        DIRECTORY_ITEMS.add(item)
        DIRECTORY_ITEM_MAP.put(item.peopleUrl, item)
    }

    private fun createDirectoryEntry(position: Int): StarWarsDirectoryItem {
        return StarWarsDirectoryItem(
            position.toString(),
            "Item $position",
            makeDetails(position),
            "remove once completed 1",
            "remove once completed 2",
            "remove once completed 3"
        )
    }

    private fun makeDetails(position: Int): String {
        val builder = StringBuilder()
        builder.append("Details about Item: ").append(position)
        for (i in 0 until position) {
            builder.append("\nMore details information here.")
        }
        return builder.toString()
    }
}
