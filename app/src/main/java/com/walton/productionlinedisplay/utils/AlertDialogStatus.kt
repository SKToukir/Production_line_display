package com.walton.productionlinedisplay.utils

sealed class AlertDialogStatus<T> constructor(val message: String? = null) {
    class Connected<T>(message: String?) : AlertDialogStatus<T>(message)
    class ConnectionLost<T>(message: String?) : AlertDialogStatus<T>(message)
    class ConnectionNotAvailable<T>(message: String?) : AlertDialogStatus<T>(message)
}