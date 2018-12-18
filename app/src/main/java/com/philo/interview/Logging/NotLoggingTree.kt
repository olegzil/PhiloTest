package com.philo.interview.Logging

import timber.log.Timber

class NotLoggingTree : Timber.Tree(){
    override fun log(priority: Int, tag: String?, message: String?, t: Throwable?) {
        //Eat all log requests for production
    }

}