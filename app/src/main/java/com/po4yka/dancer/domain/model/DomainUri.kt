package com.po4yka.dancer.domain.model

/**
 * Domain model representing a URI (Uniform Resource Identifier).
 *
 * This abstraction allows the domain layer to remain independent of Android framework
 * classes like android.net.Uri. The data layer and UI layer are responsible for
 * converting between android.net.Uri and this domain model.
 *
 * This simple string-based representation is sufficient for the domain layer's needs,
 * which primarily involves passing URIs between layers without performing URI-specific
 * operations (which would require Android framework classes).
 *
 * @property value The string representation of the URI (e.g., "content://...", "file://...")
 */
@JvmInline
value class DomainUri(val value: String) {
    /**
     * Returns the string representation of this URI.
     */
    override fun toString(): String = value

    companion object {
        /**
         * Creates a DomainUri from a string.
         *
         * @param uriString The URI string
         * @return DomainUri instance
         */
        fun fromString(uriString: String): DomainUri = DomainUri(uriString)

        /**
         * An empty URI instance for default values.
         */
        val EMPTY = DomainUri("")
    }
}
