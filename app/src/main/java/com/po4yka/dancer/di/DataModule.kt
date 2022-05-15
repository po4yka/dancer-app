package com.po4yka.dancer.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.po4yka.dancer.data.adapter.ImageProxyAdapter
import com.po4yka.dancer.data.adapter.UriAdapter
import com.po4yka.dancer.data.datasource.PoseClassifierDataSource
import com.po4yka.dancer.data.local.DancerDatabase
import com.po4yka.dancer.data.local.dao.PoseAnalysisDao
import com.po4yka.dancer.data.repository.ConfigurationRepositoryImpl
import com.po4yka.dancer.data.repository.ImageRepositoryImpl
import com.po4yka.dancer.data.repository.PoseRepositoryImpl
import com.po4yka.dancer.domain.repository.ConfigurationRepository
import com.po4yka.dancer.domain.repository.ImageRepository
import com.po4yka.dancer.domain.repository.PoseRepository
import com.po4yka.dancer.ml.Dancer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

/**
 * Extension property for creating a DataStore instance.
 *
 * This uses the preferencesDataStore delegate to create a singleton DataStore
 * instance that's scoped to the application context.
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "dancer_preferences",
)

/**
 * Data layer Hilt module that provides repositories and data sources.
 *
 * This module is installed in [SingletonComponent], making all provided dependencies
 * available as application-wide singletons.
 *
 * Currently provides:
 * - PoseRepository: Manages pose detection and classification operations
 * - PoseClassifierDataSource: Handles TensorFlow Lite model inference
 * - ConfigurationRepository: Manages app configuration and user preferences
 * - DataStore: Provides persistent storage for preferences
 * - DancerDatabase: Room database for pose analysis persistence
 * - ImageRepository: Manages image analysis storage and retrieval
 */
@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    /**
     * Provides the PoseClassifierDataSource for TensorFlow Lite operations.
     *
     * This data source handles the actual pose classification using the TensorFlow Lite model.
     * It performs image preprocessing, model inference, and output processing.
     *
     * @param model The TensorFlow Lite Dancer model from ModelModule
     * @return PoseClassifierDataSource instance configured with the model
     */
    @Provides
    @Singleton
    fun providePoseClassifierDataSource(model: Dancer): PoseClassifierDataSource = PoseClassifierDataSource(model)

    /**
     * Provides the PoseRepository implementation.
     *
     * This repository serves as the main entry point for pose recognition operations.
     * It abstracts the underlying TensorFlow Lite implementation and provides a clean API
     * for ViewModels and other consumers.
     *
     * The repository uses constructor-based dependency injection for better testability
     * and separation of concerns:
     * - PoseClassifierDataSource: Handles the actual ML inference
     * - IoDispatcher: Used for lifecycle operations (start/stop)
     *
     * @param dataSource The data source for performing pose classification
     * @param ioDispatcher Coroutine dispatcher for IO-bound operations
     * @return PoseRepository instance
     */
    @Provides
    @Singleton
    fun providePoseRepository(
        dataSource: PoseClassifierDataSource,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): PoseRepository = PoseRepositoryImpl(dataSource, ioDispatcher)

    /**
     * Provides the DataStore instance for preferences storage.
     *
     * This uses the preferencesDataStore delegate to create a singleton DataStore
     * that persists user preferences across app restarts. The DataStore provides
     * type-safe, asynchronous data storage with Flow support.
     *
     * @param context Application context
     * @return DataStore<Preferences> instance for storing app preferences
     */
    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = context.dataStore

    /**
     * Provides the ConfigurationRepository implementation.
     *
     * This repository manages all camera and analysis configuration settings,
     * providing persistent storage via DataStore and reactive updates via Flow.
     * It serves as the main entry point for accessing and modifying app configuration.
     *
     * @param dataStore DataStore instance for persisting preferences
     * @return ConfigurationRepository instance
     */
    @Provides
    @Singleton
    fun provideConfigurationRepository(dataStore: DataStore<Preferences>): ConfigurationRepository = ConfigurationRepositoryImpl(dataStore)

    /**
     * Provides the Room database instance for the application.
     *
     * This database stores pose analysis results and related metadata.
     * Uses fallbackToDestructiveMigration for development - in production,
     * proper migration strategies should be implemented.
     *
     * @param context Application context
     * @return DancerDatabase instance
     */
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): DancerDatabase {
        return Room.databaseBuilder(
            context,
            DancerDatabase::class.java,
            DancerDatabase.DATABASE_NAME,
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    /**
     * Provides the PoseAnalysisDao for database operations.
     *
     * This DAO provides access to pose analysis CRUD operations.
     *
     * @param database The DancerDatabase instance
     * @return PoseAnalysisDao instance
     */
    @Provides
    @Singleton
    fun providePoseAnalysisDao(database: DancerDatabase): PoseAnalysisDao = database.poseAnalysisDao()

    /**
     * Provides the ImageRepository implementation.
     *
     * This repository manages the persistence of image analysis results,
     * providing a clean API for use cases to store and retrieve pose analyses.
     *
     * @param dao The PoseAnalysisDao for database operations
     * @param uriAdapter Adapter for converting between domain and Android URIs
     * @return ImageRepository instance
     */
    @Provides
    @Singleton
    fun provideImageRepository(
        dao: PoseAnalysisDao,
        uriAdapter: UriAdapter,
    ): ImageRepository = ImageRepositoryImpl(dao, uriAdapter)

    /**
     * Provides the UriAdapter for converting between domain and Android URIs.
     *
     * This adapter allows the data layer to convert between the framework-agnostic
     * DomainUri and the Android-specific android.net.Uri.
     *
     * @return UriAdapter instance
     */
    @Provides
    @Singleton
    fun provideUriAdapter(): UriAdapter = UriAdapter()

    /**
     * Provides the ImageProxyAdapter for converting ImageProxy to ImageData.
     *
     * This adapter allows the presentation layer to convert camera images
     * to domain models before passing them to use cases.
     *
     * @return ImageProxyAdapter instance
     */
    @Provides
    @Singleton
    fun provideImageProxyAdapter(): ImageProxyAdapter = ImageProxyAdapter()
}
