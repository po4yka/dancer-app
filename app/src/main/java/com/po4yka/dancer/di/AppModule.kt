package com.po4yka.dancer.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Application-level Hilt module that provides core dependencies used throughout the app.
 *
 * This module is installed in [SingletonComponent], making all provided dependencies
 * available as application-wide singletons.
 *
 * Provides:
 * - Application Context
 * - Coroutine Dispatchers (IO, Main, Default) for structured concurrency
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    /**
     * Provides the application context.
     *
     * @param context The application context injected by Hilt
     * @return Application context for use throughout the app
     */
    @Provides
    @Singleton
    fun provideApplicationContext(
        @ApplicationContext context: Context,
    ): Context = context

    /**
     * Provides the IO coroutine dispatcher for I/O-bound operations.
     *
     * This dispatcher is optimized for offloading blocking IO tasks to a shared pool of threads.
     * Use for:
     * - Network requests
     * - Database operations
     * - File I/O
     *
     * @return IO dispatcher from kotlinx.coroutines
     */
    @Provides
    @Singleton
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    /**
     * Provides the Main coroutine dispatcher for UI operations.
     *
     * This dispatcher is confined to the main thread and should be used for:
     * - UI updates
     * - View interactions
     * - LiveData/StateFlow emissions
     *
     * @return Main dispatcher from kotlinx.coroutines
     */
    @Provides
    @Singleton
    @MainDispatcher
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    /**
     * Provides the Default coroutine dispatcher for CPU-intensive operations.
     *
     * This dispatcher is optimized for CPU-intensive work outside of Main thread.
     * Use for:
     * - Complex computations
     * - Data processing
     * - Sorting/filtering large datasets
     *
     * @return Default dispatcher from kotlinx.coroutines
     */
    @Provides
    @Singleton
    @DefaultDispatcher
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
}

/**
 * Qualifier annotation for identifying the IO coroutine dispatcher.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

/**
 * Qualifier annotation for identifying the Main coroutine dispatcher.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainDispatcher

/**
 * Qualifier annotation for identifying the Default coroutine dispatcher.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultDispatcher
