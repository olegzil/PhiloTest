package com.philo.interview.interfaces

interface RecyclerAdapterInterface<T> {
    fun update(newItems:List<T>)
    fun getItemDetailByPosition(index: Int): T
    fun getAllItems(): List<T>
}