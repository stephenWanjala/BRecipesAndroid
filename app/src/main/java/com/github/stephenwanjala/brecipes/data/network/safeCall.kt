package com.github.stephenwanjala.brecipes.data.network

import io.ktor.client.statement.HttpResponse
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.ensureActive
import kotlinx.serialization.SerializationException
import kotlin.coroutines.coroutineContext

suspend inline fun <reified T> safeCall(
    execute: () -> HttpResponse
): Result<T, NetworkError> {
    val response = try {
        execute()

    } catch (e: UnresolvedAddressException) {
        e.printStackTrace()
        return Result.Error(NetworkError.NoInternet)
    } catch (e: SerializationException) {
        e.printStackTrace()
        return Result.Error(NetworkError.Serialization)
    } catch (e: Exception) {
        e.printStackTrace()
        coroutineContext.ensureActive()
        return Result.Error(NetworkError.Unknown)
    }

    return responseToResult(response)
}