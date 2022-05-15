package com.po4yka.dancer.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.po4yka.dancer.data.local.dao.PoseAnalysisDao
import com.po4yka.dancer.data.local.entity.PoseAnalysisEntity

/**
 * Room database for the Dancer app.
 *
 * This database stores pose analysis results and related metadata.
 * It provides persistence for:
 * - Image URIs and analysis results
 * - Pose detection metadata
 * - Analysis history
 *
 * Database configuration:
 * - Version: 1 (initial schema)
 * - Entities: PoseAnalysisEntity
 * - DAOs: PoseAnalysisDao
 *
 * Migration strategy:
 * - For development, destructive migrations are acceptable
 * - For production, proper migrations should be implemented when schema changes
 *
 * Usage:
 * The database instance is provided by DataModule and should be accessed
 * through repositories rather than directly.
 */
@Database(
    entities = [PoseAnalysisEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class DancerDatabase : RoomDatabase() {
    /**
     * Provides access to pose analysis data access operations.
     *
     * @return PoseAnalysisDao instance for querying and modifying pose analyses
     */
    abstract fun poseAnalysisDao(): PoseAnalysisDao

    companion object {
        /**
         * Database name used for Room database creation.
         */
        const val DATABASE_NAME = "dancer_database"
    }
}
