package com.po4yka.dancer.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.po4yka.dancer.data.local.entity.PoseAnalysisEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for pose analysis database operations.
 *
 * This DAO provides reactive access to pose analysis records using Kotlin Flow.
 * All database operations are performed asynchronously to maintain app responsiveness.
 *
 * Room automatically handles:
 * - Thread safety
 * - Flow emissions on database changes
 * - Query optimization
 */
@Dao
interface PoseAnalysisDao {
    /**
     * Retrieves all pose analyses ordered by timestamp (most recent first).
     *
     * Returns a Flow that emits the updated list whenever the database changes.
     * This is ideal for displaying a history/gallery of analyzed images.
     *
     * @return Flow emitting list of all pose analyses, ordered by timestamp descending
     */
    @Query("SELECT * FROM pose_analyses ORDER BY timestamp DESC")
    fun getAllAnalyses(): Flow<List<PoseAnalysisEntity>>

    /**
     * Retrieves a specific pose analysis by its ID.
     *
     * Returns a Flow that emits the analysis if found, or null if not present.
     * The Flow will emit updates if the analysis record is modified.
     *
     * @param id Unique identifier of the analysis to retrieve
     * @return Flow emitting the analysis or null if not found
     */
    @Query("SELECT * FROM pose_analyses WHERE id = :id")
    fun getAnalysisById(id: String): Flow<PoseAnalysisEntity?>

    /**
     * Inserts a new pose analysis record into the database.
     *
     * Uses REPLACE conflict strategy to update existing records with the same ID.
     * This allows re-analyzing images and updating their results.
     *
     * @param entity The pose analysis entity to insert or replace
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnalysis(entity: PoseAnalysisEntity)

    /**
     * Deletes a specific pose analysis by its ID.
     *
     * This removes the database record but does not delete the actual image file.
     * Image file cleanup should be handled separately.
     *
     * @param id Unique identifier of the analysis to delete
     */
    @Query("DELETE FROM pose_analyses WHERE id = :id")
    suspend fun deleteAnalysis(id: String)

    /**
     * Deletes all pose analyses from the database.
     *
     * Useful for clearing history or resetting the app.
     * This does not delete the actual image files.
     */
    @Query("DELETE FROM pose_analyses")
    suspend fun deleteAll()

    /**
     * Counts the total number of stored pose analyses.
     *
     * @return Flow emitting the current count of analyses
     */
    @Query("SELECT COUNT(*) FROM pose_analyses")
    fun getAnalysisCount(): Flow<Int>
}
