package com.yugentech.ryori.api.error

import com.yugentech.ryori.R
import androidx.annotation.StringRes
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.serialization.SerializationException
import java.io.IOException
import java.net.ConnectException
import java.net.NoRouteToHostException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.nio.channels.UnresolvedAddressException

// What went wrong, in words a user can act on. Screens never show raw exception messages
// ("Unable to resolve host www.themealdb.com...", "HTTP 503 for ..."); every failure is mapped
// to one of these with Throwable.toAppError().
//   title / message -> full-screen error states
//   short           -> one-line toasts for actions that failed on an otherwise working screen
enum class AppError(
    @param:StringRes val title: Int,
    @param:StringRes val message: Int,
    @param:StringRes val short: Int
) {
    OFFLINE(
        title = R.string.error_offline_title,
        message = R.string.error_offline_message,
        short = R.string.error_offline_short
    ),
    TIMEOUT(
        title = R.string.error_timeout_title,
        message = R.string.error_timeout_message,
        short = R.string.error_timeout_short
    ),
    SERVER(
        title = R.string.error_server_title,
        message = R.string.error_server_message,
        short = R.string.error_server_short
    ),
    NOT_FOUND(
        title = R.string.error_not_found_title,
        message = R.string.error_not_found_message,
        short = R.string.error_not_found_short
    ),
    UNKNOWN(
        title = R.string.error_unknown_title,
        message = R.string.error_unknown_message,
        short = R.string.error_unknown_short
    )
}

// Thrown by the network layer for a non-2xx response.
class HttpStatusException(val status: Int, url: String) : IOException("HTTP $status for $url")

// Thrown when a lookup comes back empty (unknown id, no random pick available).
class NotFoundException(message: String) : Exception(message)

fun Throwable.toAppError(): AppError {
    // Ktor/OkHttp often wrap the real cause, so look down the whole chain.
    val chain = generateSequence(this) { it.cause }.take(8).toList()
    return when {
        // Timeouts first: some timeout types extend ConnectException / IOException.
        chain.any {
            it is SocketTimeoutException || it is HttpRequestTimeoutException || it is ConnectTimeoutException
        } -> AppError.TIMEOUT

        chain.any { it is NotFoundException } -> AppError.NOT_FOUND

        chain.any { it is HttpStatusException } -> AppError.SERVER

        // A response we couldn't read is the server's problem, not the user's.
        chain.any { it is SerializationException } -> AppError.SERVER

        chain.any {
            it is UnknownHostException || it is ConnectException ||
                it is NoRouteToHostException || it is UnresolvedAddressException
        } -> AppError.OFFLINE

        // Anything else on the wire (dropped connection, TLS failure on captive Wi-Fi...).
        chain.any { it is IOException } -> AppError.OFFLINE

        else -> AppError.UNKNOWN
    }
}

// Result helper for view models: the friendly error, or null on success.
fun Result<*>.appErrorOrNull(): AppError? = exceptionOrNull()?.toAppError()
