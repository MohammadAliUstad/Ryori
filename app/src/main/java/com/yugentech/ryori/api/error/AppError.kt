package com.yugentech.ryori.api.error

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
enum class AppError(val title: String, val message: String, val short: String) {
    OFFLINE(
        title = "You're offline",
        message = "Check your Wi-Fi or mobile data, then try again.",
        short = "No internet connection"
    ),
    TIMEOUT(
        title = "This is taking too long",
        message = "The recipe server is slow to respond. Please try again in a moment.",
        short = "The connection timed out. Try again."
    ),
    SERVER(
        title = "The kitchen's closed for a moment",
        message = "The recipe server is having trouble right now. Please try again a little later.",
        short = "The recipe server is having trouble"
    ),
    NOT_FOUND(
        title = "Recipe not found",
        message = "This recipe isn't available anymore. Try another one.",
        short = "Couldn't find a recipe. Try again."
    ),
    UNKNOWN(
        title = "Something went wrong",
        message = "An unexpected problem occurred. Please try again.",
        short = "Something went wrong. Try again."
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
