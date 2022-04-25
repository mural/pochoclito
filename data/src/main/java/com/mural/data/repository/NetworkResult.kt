package com.mural.data.repository

sealed class NetworkResult<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Loading<T> : NetworkResult<T>() {
        override fun equals(other: Any?): Boolean {
            return other is Loading<*>
        }
    }

    class Cached<T>(data: T?) : NetworkResult<T>(data)
    class Success<T>(data: T?) : NetworkResult<T>(data)
    class Error<T>(message: String?, data: T? = null) : NetworkResult<T>(data, message)

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is NetworkResult<*> -> {
                this.data == other.data
            }
            else -> false
        }
    }
}