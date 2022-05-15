package com.po4yka.dancer.data.models

/**
 * A sealed class representing the result of an operation that can either succeed or fail.
 *
 * This class provides a type-safe way to handle operations that may fail without using exceptions
 * for control flow. It encapsulates both successful results and error states.
 *
 * @param T The type of data returned on success
 */
sealed class Result<out T> {
    /**
     * Represents a successful operation result.
     *
     * @param T The type of data contained in the successful result
     * @property data The data returned by the successful operation
     */
    data class Success<T>(val data: T) : Result<T>()

    /**
     * Represents a failed operation result.
     *
     * @property exception The exception that caused the failure
     * @property message A human-readable error message describing the failure
     */
    data class Error(
        val exception: Exception,
        val message: String,
    ) : Result<Nothing>()

    /**
     * Returns true if this result is a success, false otherwise.
     */
    val isSuccess: Boolean
        get() = this is Success

    /**
     * Returns true if this result is an error, false otherwise.
     */
    val isError: Boolean
        get() = this is Error

    /**
     * Returns the data if this is a Success, or null if this is an Error.
     */
    fun getOrNull(): T? =
        when (this) {
            is Success -> data
            is Error -> null
        }

    /**
     * Returns the data if this is a Success, or throws the exception if this is an Error.
     */
    fun getOrThrow(): T =
        when (this) {
            is Success -> data
            is Error -> throw exception
        }
}

/**
 * Extension function to convert a nullable value to a Result.
 *
 * @param onNull A function to provide an error message if the value is null
 * @return Result.Success if the value is not null, Result.Error otherwise
 */
inline fun <T> T?.toResult(onNull: () -> String): Result<T> =
    this?.let {
        Result.Success(it)
    } ?: Result.Error(
        NullPointerException("Value is null"),
        onNull(),
    )

/**
 * Extension function to safely execute a block of code and wrap the result in a Result.
 *
 * @param block The block of code to execute
 * @return Result.Success with the result if successful, Result.Error if an exception is thrown
 */
inline fun <T> runCatchingAsResult(block: () -> T): Result<T> =
    try {
        Result.Success(block())
    } catch (e: Exception) {
        Result.Error(e, e.message ?: "Unknown error occurred")
    }
