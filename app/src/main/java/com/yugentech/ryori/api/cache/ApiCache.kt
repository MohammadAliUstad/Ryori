package com.yugentech.ryori.api.cache

import android.util.LruCache
import com.yugentech.ryori.data.local.ApiCacheDao
import com.yugentech.ryori.data.local.ApiCacheEntity
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import kotlin.time.Duration

// Two-level cache for raw API response bodies, keyed by full request URL:
//   1. memory (LruCache) for instant repeat reads within a session
//   2. Room (api_cache table) so data survives restarts and works offline
// Entries store when they were saved; freshness is judged by the caller's TTL, so the same
// entry can be "fresh" for one use and still usable as an offline fallback for another.
class ApiCache(private val dao: ApiCacheDao) {

    data class Entry(val body: String, val savedAtMillis: Long) {
        fun isFresh(ttl: Duration, now: Long = System.currentTimeMillis()): Boolean =
            now - savedAtMillis < ttl.inWholeMilliseconds
    }

    private val memory = LruCache<String, Entry>(MEMORY_ENTRIES)

    // Approximate cached size in bytes, shown on the More screen's Storage row.
    val sizeBytes: Flow<Long> = dao.sizeBytes()

    suspend fun get(key: String): Entry? {
        memory.get(key)?.let { return it }
        return runCatching { dao.get(key) }
            .onFailure { Timber.w(it, "ApiCache: couldn't read entry") }
            .getOrNull()
            ?.let { Entry(it.body, it.savedAtMillis) }
            ?.also { memory.put(key, it) }
    }

    suspend fun put(key: String, body: String) {
        val entry = Entry(body, System.currentTimeMillis())
        memory.put(key, entry)
        runCatching { dao.put(ApiCacheEntity(key = key, body = body, savedAtMillis = entry.savedAtMillis)) }
            .onFailure { Timber.w(it, "ApiCache: couldn't write entry") }
    }

    suspend fun clear() {
        memory.evictAll()
        runCatching { dao.clear() }
    }

    private companion object {
        const val MEMORY_ENTRIES = 256
    }
}
