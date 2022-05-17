package com.po4yka.dancer.di

import com.po4yka.dancer.domain.repository.ConfigurationRepository
import com.po4yka.dancer.domain.repository.ImageRepository
import com.po4yka.dancer.domain.repository.PoseRepository
import com.po4yka.dancer.domain.usecase.AnalyzePoseUseCase
import com.po4yka.dancer.domain.usecase.DeleteImageUseCase
import com.po4yka.dancer.domain.usecase.GetCameraConfigurationUseCase
import com.po4yka.dancer.domain.usecase.LoadImageUseCase
import com.po4yka.dancer.domain.usecase.SaveImageAnalysisUseCase
import com.po4yka.dancer.domain.usecase.StartCameraAnalysisUseCase
import com.po4yka.dancer.domain.usecase.StopCameraAnalysisUseCase
import com.po4yka.dancer.domain.usecase.UpdateCameraConfigurationUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

/**
 * Domain layer Hilt module that provides use cases.
 *
 * This module is installed in [ViewModelComponent], making all provided use cases
 * scoped to ViewModels. This ensures that:
 * - Use cases are created when a ViewModel is created
 * - Use cases are destroyed when the ViewModel is destroyed
 * - Each ViewModel gets its own instance of use cases
 *
 * The module follows clean architecture principles by keeping the domain layer
 * independent of framework details, with use cases acting as application-specific
 * business rules.
 *
 * Provides:
 * - AnalyzePoseUseCase: Analyzes individual camera frames for poses
 * - StartCameraAnalysisUseCase: Starts the camera analysis session
 * - StopCameraAnalysisUseCase: Stops the camera analysis session
 * - GetCameraConfigurationUseCase: Retrieves camera configuration settings
 * - UpdateCameraConfigurationUseCase: Updates camera configuration settings
 * - LoadImageUseCase: Loads stored image analyses
 * - SaveImageAnalysisUseCase: Saves pose analysis results
 * - DeleteImageUseCase: Deletes stored image analyses
 */
@Module
@InstallIn(ViewModelComponent::class)
object DomainModule {
    /**
     * Provides the AnalyzePoseUseCase for analyzing individual camera frames.
     *
     * This use case encapsulates the business logic for:
     * - Retrieving current configuration (threshold, mirror mode)
     * - Performing pose classification via the repository
     * - Applying threshold logic to determine detection status
     * - Returning structured results with detection state and predictions
     *
     * @param poseRepository Repository for pose classification operations
     * @param configurationRepository Repository for accessing configuration settings
     * @return AnalyzePoseUseCase instance scoped to the ViewModel lifecycle
     */
    @Provides
    @ViewModelScoped
    fun provideAnalyzePoseUseCase(
        poseRepository: PoseRepository,
        configurationRepository: ConfigurationRepository,
    ): AnalyzePoseUseCase =
        AnalyzePoseUseCase(
            poseRepository = poseRepository,
            configurationRepository = configurationRepository,
        )

    /**
     * Provides the StartCameraAnalysisUseCase for managing camera analysis lifecycle.
     *
     * This use case is responsible for:
     * - Starting the pose classification service
     * - Initializing the ML model
     * - Providing a reactive stream of configuration updates
     * - Managing the analysis state (active/inactive)
     *
     * @param poseRepository Repository for pose classification operations
     * @param configurationRepository Repository for accessing configuration settings
     * @return StartCameraAnalysisUseCase instance scoped to the ViewModel lifecycle
     */
    @Provides
    @ViewModelScoped
    fun provideStartCameraAnalysisUseCase(
        poseRepository: PoseRepository,
        configurationRepository: ConfigurationRepository,
    ): StartCameraAnalysisUseCase =
        StartCameraAnalysisUseCase(
            poseRepository = poseRepository,
            configurationRepository = configurationRepository,
        )

    /**
     * Provides the StopCameraAnalysisUseCase for stopping camera analysis.
     *
     * This use case is responsible for:
     * - Stopping the pose classification service
     * - Releasing ML model resources
     * - Cleaning up any active analysis operations
     *
     * @param poseRepository Repository for pose classification operations
     * @return StopCameraAnalysisUseCase instance scoped to the ViewModel lifecycle
     */
    @Provides
    @ViewModelScoped
    fun provideStopCameraAnalysisUseCase(poseRepository: PoseRepository): StopCameraAnalysisUseCase =
        StopCameraAnalysisUseCase(
            poseRepository = poseRepository,
        )

    /**
     * Provides the GetCameraConfigurationUseCase for retrieving configuration.
     *
     * This use case provides access to camera and analysis configuration settings,
     * offering both reactive (Flow) and one-shot access patterns to accommodate
     * different use cases in the presentation layer.
     *
     * @param configurationRepository Repository for accessing configuration settings
     * @return GetCameraConfigurationUseCase instance scoped to the ViewModel lifecycle
     */
    @Provides
    @ViewModelScoped
    fun provideGetCameraConfigurationUseCase(
        configurationRepository: ConfigurationRepository,
    ): GetCameraConfigurationUseCase =
        GetCameraConfigurationUseCase(
            configurationRepository = configurationRepository,
        )

    /**
     * Provides the UpdateCameraConfigurationUseCase for modifying configuration.
     *
     * This use case encapsulates the business logic for modifying camera and
     * analysis configuration settings. It provides a clean API for updating
     * configuration values while maintaining validation and consistency rules.
     *
     * @param configurationRepository Repository for managing configuration settings
     * @return UpdateCameraConfigurationUseCase instance scoped to the ViewModel lifecycle
     */
    @Provides
    @ViewModelScoped
    fun provideUpdateCameraConfigurationUseCase(
        configurationRepository: ConfigurationRepository,
    ): UpdateCameraConfigurationUseCase =
        UpdateCameraConfigurationUseCase(
            configurationRepository = configurationRepository,
        )

    /**
     * Provides the LoadImageUseCase for loading stored image analyses.
     *
     * This use case retrieves pose analysis results from the database,
     * providing access to both individual analyses and the complete history.
     *
     * @param imageRepository Repository for accessing stored analyses
     * @return LoadImageUseCase instance scoped to the ViewModel lifecycle
     */
    @Provides
    @ViewModelScoped
    fun provideLoadImageUseCase(imageRepository: ImageRepository): LoadImageUseCase =
        LoadImageUseCase(
            imageRepository = imageRepository,
        )

    /**
     * Provides the SaveImageAnalysisUseCase for persisting analysis results.
     *
     * This use case handles the storage of pose analysis results along with
     * image URIs and metadata, generating unique IDs for each analysis.
     *
     * @param imageRepository Repository for storing analyses
     * @return SaveImageAnalysisUseCase instance scoped to the ViewModel lifecycle
     */
    @Provides
    @ViewModelScoped
    fun provideSaveImageAnalysisUseCase(imageRepository: ImageRepository): SaveImageAnalysisUseCase =
        SaveImageAnalysisUseCase(
            imageRepository = imageRepository,
        )

    /**
     * Provides the DeleteImageUseCase for removing stored analyses.
     *
     * This use case handles the deletion of pose analysis records,
     * providing both single-item and bulk deletion capabilities.
     *
     * @param imageRepository Repository for managing analyses
     * @return DeleteImageUseCase instance scoped to the ViewModel lifecycle
     */
    @Provides
    @ViewModelScoped
    fun provideDeleteImageUseCase(imageRepository: ImageRepository): DeleteImageUseCase =
        DeleteImageUseCase(
            imageRepository = imageRepository,
        )
}
