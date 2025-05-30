package com.github.stephenwanjala.brecipes.data.network

sealed interface NetworkError : Error {
    data object RequestTimeout : NetworkError
    data object TooManyRequests : NetworkError
    data object NoInternet : NetworkError
    data object ServerError : NetworkError
    data object Serialization : NetworkError
    data object Unknown : NetworkError
    data class BadRequest(val errorBody: String?) : NetworkError
    data object Unauthorized : NetworkError
    data object Forbidden : NetworkError
    data class LicenseError(val message:String) :NetworkError
}


sealed interface UiError : Error {
    data class EmptyLicense(val value:String):UiError

}