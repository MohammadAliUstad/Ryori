package com.yugentech.ryori.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

// Ryori's on-device database:
//   api_cache       raw API responses (the disk layer behind ApiCache), so data loads instantly
//                   and still works offline
//   recent_recipes  recipes the user opened, for "Recently viewed" on the More screen
//   kitchen_stats   simple named counters (recipes viewed, ingredients ticked, ...)
//   cuisine_stats   how many recipes of each cuisine were opened, for "favourite cuisine"
@Database(
    entities = [
        ApiCacheEntity::class,
        RecentRecipeEntity::class,
        KitchenStatEntity::class,
        CuisineStatEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class RyoriDatabase : RoomDatabase() {
    abstract fun apiCacheDao(): ApiCacheDao
    abstract fun recentRecipeDao(): RecentRecipeDao
    abstract fun kitchenStatsDao(): KitchenStatsDao

    companion object {
        const val NAME = "ryori.db"
    }
}

// --- Entities ---------------------------------------------------------------------------------

@Entity(tableName = "api_cache")
data class ApiCacheEntity(
    @PrimaryKey val key: String,
    val body: String,
    val savedAtMillis: Long
)

@Entity(tableName = "recent_recipes")
data class RecentRecipeEntity(
    // "MEAL_52772" / "DRINK_11007": ids are only unique within each API.
    @PrimaryKey val key: String,
    val id: String,
    val type: String,
    val name: String,
    val image: String?,
    val viewedAtMillis: Long
)

@Entity(tableName = "kitchen_stats")
data class KitchenStatEntity(
    @PrimaryKey val name: String,
    val value: Long
)

@Entity(tableName = "cuisine_stats")
data class CuisineStatEntity(
    @PrimaryKey val area: String,
    val views: Long
)

// --- DAOs -------------------------------------------------------------------------------------

@Dao
interface ApiCacheDao {
    @Query("SELECT * FROM api_cache WHERE `key` = :key")
    suspend fun get(key: String): ApiCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun put(entry: ApiCacheEntity)

    @Query("DELETE FROM api_cache")
    suspend fun clear()

    // Approximate size of the cached text, for the Storage row on the More screen.
    @Query("SELECT COALESCE(SUM(LENGTH(body)), 0) FROM api_cache")
    fun sizeBytes(): Flow<Long>
}

@Dao
interface RecentRecipeDao {
    @Upsert
    suspend fun upsert(recipe: RecentRecipeEntity)

    @Query("SELECT * FROM recent_recipes ORDER BY viewedAtMillis DESC LIMIT :limit")
    fun recent(limit: Int): Flow<List<RecentRecipeEntity>>

    @Query("SELECT COUNT(*) FROM recent_recipes")
    fun count(): Flow<Int>

    // Keeps the history to the newest [keep] entries.
    @Query(
        "DELETE FROM recent_recipes WHERE `key` NOT IN " +
            "(SELECT `key` FROM recent_recipes ORDER BY viewedAtMillis DESC LIMIT :keep)"
    )
    suspend fun trim(keep: Int)

    @Query("DELETE FROM recent_recipes")
    suspend fun clear()
}

@Dao
interface KitchenStatsDao {
    @Query("SELECT COALESCE((SELECT value FROM kitchen_stats WHERE name = :name), 0)")
    fun counter(name: String): Flow<Long>

    @Query(
        "INSERT INTO kitchen_stats (name, value) VALUES (:name, :by) " +
            "ON CONFLICT(name) DO UPDATE SET value = value + :by"
    )
    suspend fun increment(name: String, by: Long = 1)

    @Query(
        "INSERT INTO cuisine_stats (area, views) VALUES (:area, 1) " +
            "ON CONFLICT(area) DO UPDATE SET views = views + 1"
    )
    suspend fun incrementCuisine(area: String)

    @Query("SELECT area FROM cuisine_stats ORDER BY views DESC LIMIT 1")
    fun favouriteCuisine(): Flow<String?>
}
