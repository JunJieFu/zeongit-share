package com.zeongit.share.exception

open class BaseException : RuntimeException  {
    var status: Int = 500

    var data: Any? = null

    constructor(message: String?, status: Int, data: Any?) : super(message) {
        this.status = status
        this.data = data
    }

    constructor(message: String?, status: Int) : super(message) {
        this.status = status
    }

    constructor(message: String?) : super(message) {}
}