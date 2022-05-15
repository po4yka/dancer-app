package com.po4yka.dancer.domain.model

/**
 * Domain model representing image data for pose analysis.
 *
 * This abstraction allows the domain layer to remain independent of Android framework
 * classes like ImageProxy. The data layer is responsible for converting between
 * framework types and this domain model.
 *
 * @property width Image width in pixels
 * @property height Image height in pixels
 * @property rotation Image rotation in degrees (0, 90, 180, 270)
 * @property format Image format (RGBA_8888, YUV_420_888, JPEG, etc.)
 * @property timestamp Image capture timestamp in nanoseconds
 * @property planes Image data planes containing the actual pixel data
 */
data class ImageData(
    val width: Int,
    val height: Int,
    val rotation: Int,
    val format: ImageFormat,
    val timestamp: Long,
    val planes: List<ImagePlane>,
)

/**
 * Represents an image data plane.
 *
 * Images may have multiple planes depending on the format (e.g., YUV has 3 planes).
 * Each plane contains a portion of the image data along with stride information.
 *
 * @property buffer The raw byte data for this plane
 * @property rowStride Number of bytes between consecutive rows in the plane
 * @property pixelStride Number of bytes between consecutive pixels in the plane
 */
data class ImagePlane(
    val buffer: ByteArray,
    val rowStride: Int,
    val pixelStride: Int,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImagePlane

        if (!buffer.contentEquals(other.buffer)) return false
        if (rowStride != other.rowStride) return false
        if (pixelStride != other.pixelStride) return false

        return true
    }

    override fun hashCode(): Int {
        var result = buffer.contentHashCode()
        result = 31 * result + rowStride
        result = 31 * result + pixelStride
        return result
    }
}

/**
 * Enumeration of supported image formats.
 *
 * This represents the common image formats used in the application,
 * abstracting away the Android-specific format constants.
 */
enum class ImageFormat {
    /**
     * RGB format with 8 bits per color channel plus alpha.
     * 4 bytes per pixel.
     */
    RGBA_8888,

    /**
     * YUV format with 4:2:0 chroma subsampling.
     * Commonly used for camera preview and video.
     */
    YUV_420_888,

    /**
     * JPEG compressed format.
     */
    JPEG,

    /**
     * Unknown or unsupported format.
     */
    UNKNOWN,
}
