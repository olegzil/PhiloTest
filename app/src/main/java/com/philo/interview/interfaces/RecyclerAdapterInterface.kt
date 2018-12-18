package com.philo.interview.interfaces

interface RecyclerAdapterInterface<T> {
    fun update()
    fun getItemDetailByPosition(index: Int): T
    fun getAllItems(): List<T>
}