package com.yugentech.ryori.api.cache

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.URLBuilder
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import timber.log.Timber
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

// How long each kind of response is served from cache before refetching.
object CachePolicy {
    val STATIC: Duration = 7.days      // categories, cuisines, ingredient list, lookups by id
    val LISTS: Duration = 1.days       // filtered lists (by category / cuisine / ingredient...)
    val SEARCH: Duration = 1.hours     // free-text search
    val NONE: Duration = Duration.ZERO // random endpoints: always fresh
}

// Network layer with the ApiCache in front of it:
//   fresh cached copy  -> returned without touching the network
//   stale / missing    -> fetched, decoded, and only then cached (bad responses never are)
//   network fails      -> falls back to the stale copy if there is one (offline support)
class CachedApi(
    private val client: HttpClient,
    private val cache: ApiCache,
    private val json: Json
) {

    suspend fun <T> get(
        url: String,
        params: Map<String, String>,
        ttl: Duration,
        deserializer: DeserializationStrategy<T>
    ): T {
        if (ttl <= Duration.ZERO) return json.decodeFromString(deserializer, fetch(url, params))

        val key = cacheKey(url, params)
        val cached = cache.get(key)
        if (cached != null && cached.isFresh(ttl)) {
            runCatching { return json.decodeFromString(deserializer, cached.body) }
        }

        return try {
            val body = fetch(url, params)
            val decoded = json.decodeFromString(deserializer, body)
            cache.put(key, body)
            decoded
        } catch (e: Exception) {
            // Offline or server trouble: serve whatever we had, however old.
            val fallback = cached?.let { runCatching { json.decodeFromString(deserializer, it.body) }.getOrNull() }
            if (fallback != null) {
                Timber.i("CachedApi: network failed, serving stale cache for $url")
                fallback
            } else {
                throw e
            }
        }
    }

    suspend inline fun <reified T> get(
        url: String,
        params: Map<String, String> = emptyMap(),
        ttl: Duration
    ): T = get(url, params, ttl, serializer<T>())

    suspend fun clear() = cache.clear()

    // Approximate size of all cached responses, in bytes.
    val sizeBytes: Flow<Long> get() = cache.sizeBytes

    private suspend fun fetch(url: String, params: Map<String, String>): String {
        val response = client.get(url) {
            params.forEach { (name, value) -> parameter(name, value) }
        }
        check(response.status.isSuccess()) { "HTTP ${response.status.value} for $url" }
        return response.bodyAsText()
    }

    // Parameters sorted so the same request always maps to the same entry.
    private fun cacheKey(url: String, params: Map<String, String>): String =
        URLBuilder(url).apply {
            params.toSortedMap().forEach { (name, value) -> parameters.append(name, value) }
        }.buildString()
}
