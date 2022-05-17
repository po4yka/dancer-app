package com.po4yka.dancer.data.repository

import com.po4yka.dancer.data.local.dao.PoseAnalysisDao
import com.po4yka.dancer.data.local.entity.PoseAnalysisEntity
import com.po4yka.dancer.domain.model.DomainUri
import com.po4yka.dancer.domain.model.PoseAnalysisResult
import com.po4yka.dancer.domain.model.PosePrediction
import com.po4yka.dancer.domain.repository.ImageAnalysisData
import com.po4yka.dancer.domain.repository.ImageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject

/**
 * Implementation of ImageRepository using Room database for local persistence.
 *
 * This repository manages the conversion between domain models and database entities,
 * handles JSON serialization for complex objects, and provides reactive data access.
 *
 * Key features:
 * - Automatic conversion between domain models and database entities
 * - JSON serialization/deserialization for predictions list
 * - Reactive data access using Kotlin Flow
 * - Error handling with logging
 * - URI adaptation between domain and Android framework types
 *
 * @property dao Data access object for pose analysis operations
 */
class ImageRepositoryImpl
    @Inject
    constructor(
        private val dao: PoseAnalysisDao,
    ) : ImageRepository {
        override suspend fun saveAnalysis(
            id: String,
            imageUri: DomainUri,
            result: PoseAnalysisResult,
            timestamp: Long,
            cameraLens: String?,
            threshold: Float,
        ) {
            try {
                val entity =
                    PoseAnalysisEntity(
                        id = id,
                        imageUri = imageUri.value,
                        timestamp = timestamp,
                        isDetected = result.isDetected,
                        confidence = result.confidence,
                        predictions = serializePredictions(result.predictions),
                        cameraLens = cameraLens,
                        threshold = threshold,
                    )
                dao.insertAnalysis(entity)
                Timber.d("Saved analysis: id=$id, detected=${result.isDetected}, confidence=${result.confidence}")
            } catch (e: Exception) {
                Timber.e(e, "Failed to save analysis: id=$id")
                throw e
            }
        }

        override fun getAnalysis(id: String): Flow<ImageAnalysisData?> {
            return dao.getAnalysisById(id).map { entity ->
                entity?.let { mapEntityToDomain(it) }
            }
        }

        override fun getAllAnalyses(): Flow<List<ImageAnalysisData>> {
            return dao.getAllAnalyses().map { entities ->
                entities.map { mapEntityToDomain(it) }
            }
        }

        override suspend fun deleteAnalysis(id: String) {
            try {
                dao.deleteAnalysis(id)
                Timber.d("Deleted analysis: id=$id")
            } catch (e: Exception) {
                Timber.e(e, "Failed to delete analysis: id=$id")
                throw e
            }
        }

        override suspend fun deleteAll() {
            try {
                dao.deleteAll()
                Timber.d("Deleted all analyses")
            } catch (e: Exception) {
                Timber.e(e, "Failed to delete all analyses")
                throw e
            }
        }

        /**
         * Converts a database entity to domain model.
         *
         * This method handles:
         * - URI parsing from string to DomainUri
         * - JSON deserialization of predictions
         * - Reconstruction of PoseAnalysisResult
         *
         * @param entity The database entity to convert
         * @return ImageAnalysisData domain model
         */
        private fun mapEntityToDomain(entity: PoseAnalysisEntity): ImageAnalysisData {
            return ImageAnalysisData(
                id = entity.id,
                imageUri = DomainUri.fromString(entity.imageUri),
                timestamp = entity.timestamp,
                result =
                    PoseAnalysisResult(
                        isDetected = entity.isDetected,
                        confidence = entity.confidence,
                        predictions = deserializePredictions(entity.predictions),
                    ),
                cameraLens = entity.cameraLens,
                threshold = entity.threshold,
            )
        }

        /**
         * Serializes a list of pose predictions to JSON string.
         *
         * Format: [{"moveName":"pose1","probability":0.85}, ...]
         *
         * @param predictions List of predictions to serialize
         * @return JSON string representation
         */
        private fun serializePredictions(predictions: List<PosePrediction>): String {
            return try {
                val jsonArray = JSONArray()
                predictions.forEach { prediction ->
                    val jsonObject =
                        JSONObject().apply {
                            put("moveName", prediction.moveName)
                            put("probability", prediction.probability)
                        }
                    jsonArray.put(jsonObject)
                }
                jsonArray.toString()
            } catch (e: Exception) {
                Timber.e(e, "Failed to serialize predictions")
                "[]"
            }
        }

        /**
         * Deserializes JSON string to list of pose predictions.
         *
         * Expected format: [{"moveName":"pose1","probability":0.85}, ...]
         *
         * @param json JSON string to deserialize
         * @return List of PosePrediction objects
         */
        private fun deserializePredictions(json: String): List<PosePrediction> {
            return try {
                val jsonArray = JSONArray(json)
                val predictions = mutableListOf<PosePrediction>()

                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val prediction =
                        PosePrediction(
                            moveName = jsonObject.getString("moveName"),
                            probability = jsonObject.getDouble("probability").toFloat(),
                        )
                    predictions.add(prediction)
                }

                predictions
            } catch (e: Exception) {
                Timber.e(e, "Failed to deserialize predictions from: $json")
                emptyList()
            }
        }
    }
