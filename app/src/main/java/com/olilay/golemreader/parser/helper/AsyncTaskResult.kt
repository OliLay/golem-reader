package com.olilay.golemreader.parser.helper

import java.lang.Exception

class AsyncTaskResult<T>(val taskResult: T) {
    var error: Exception?

    init {
        this.error = null
    }

    constructor(result: T, error: Exception) : this(result) {
        this.error = error
    }
}