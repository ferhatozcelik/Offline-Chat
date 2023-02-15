package org.turkiye.offlinechat.data.model

sealed class APIResponse<T>(
    val result: T? = null
) {
    class Success<T>(result: T? = null) : APIResponse<T>(result)
    class Error<T>(result: T? = null) : APIResponse<T>(result)
    class Loading<T> : APIResponse<T>()
}