package com.po4yka.dancer.data.adapter

import android.net.Uri
import com.po4yka.dancer.domain.model.DomainUri
import javax.inject.Inject

/**
 * Adapter for converting between Android framework Uri and domain DomainUri.
 *
 * This adapter is part of the data layer and handles the conversion between
 * the Android-specific android.net.Uri class and the framework-agnostic DomainUri
 * domain model. This allows the domain layer to remain pure Kotlin without
 * Android dependencies.
 *
 * Key responsibilities:
 * - Convert android.net.Uri to DomainUri (string representation)
 * - Convert DomainUri back to android.net.Uri for Android APIs
 */
class UriAdapter
    @Inject
    constructor() {
        /**
         * Converts an Android Uri to domain DomainUri.
         *
         * @param uri The Android Uri to convert
         * @return DomainUri containing the string representation of the Uri
         */
        fun toDomain(uri: Uri): DomainUri {
            return DomainUri(uri.toString())
        }

        /**
         * Converts a domain DomainUri to Android Uri.
         *
         * @param domainUri The domain Uri to convert
         * @return Android Uri parsed from the domain Uri string
         */
        fun toAndroid(domainUri: DomainUri): Uri {
            return Uri.parse(domainUri.value)
        }
    }
