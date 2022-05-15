package com.po4yka.dancer.data.adapter

import androidx.camera.core.ImageProxy
import com.po4yka.dancer.domain.model.ImageData
import com.po4yka.dancer.domain.model.ImageFormat
import com.po4yka.dancer.domain.model.ImagePlane
import java.nio.ByteBuffer
import javax.inject.Inject
import android.graphics.ImageFormat as AndroidImageFormat

/**
 * Adapter for converting between Android framework ImageProxy and domain ImageData.
 *
 * This adapter is part of the data layer and handles the conversion between
 * the Android-specific ImageProxy class and the framework-agnostic ImageData
 * domain model. This allows the domain layer to remain pure Kotlin without
 * Android dependencies.
 *
 * Key responsibilities:
 * - Extract image metadata (width, height, rotation, format, timestamp)
 * - Convert image format constants from Android to domain enum
 * - Extract image planes and their data
 * - Handle buffer copying for safe data access
 */
class ImageProxyAdapter
    @Inject
    constructor() {
        /**
         * Converts an ImageProxy to domain ImageData model.
         *
         * This method extracts all relevant data from the ImageProxy and packages
         * it into a domain model that can be used by the domain layer.
         *
         * Note: The ImageProxy's planes' buffers are copied to ensure thread safety
         * and to avoid issues with the ImageProxy being closed before the data is used.
         *
         * @param imageProxy The camera image proxy to convert
         * @return ImageData domain model containing all image information
         */
        @androidx.camera.core.ExperimentalGetImage
        fun toDomain(imageProxy: ImageProxy): ImageData {
            val image =
                imageProxy.image
                    ?: throw IllegalArgumentException("ImageProxy does not contain an image")

            return ImageData(
                width = imageProxy.width,
                height = imageProxy.height,
                rotation = imageProxy.imageInfo.rotationDegrees,
                format = convertFormat(imageProxy.format),
                timestamp = imageProxy.imageInfo.timestamp,
                planes =
                    image.planes.map { plane ->
                        ImagePlane(
                            buffer = copyBuffer(plane.buffer),
                            rowStride = plane.rowStride,
                            pixelStride = plane.pixelStride,
                        )
                    },
            )
        }

        /**
         * Converts Android ImageFormat constants to domain ImageFormat enum.
         *
         * @param androidFormat The Android ImageFormat constant
         * @return Corresponding ImageFormat enum value
         */
        private fun convertFormat(androidFormat: Int): ImageFormat {
            return when (androidFormat) {
                AndroidImageFormat.YUV_420_888 -> ImageFormat.YUV_420_888
                AndroidImageFormat.JPEG -> ImageFormat.JPEG
                // Note: RGBA_8888 is not typically used in CameraX, but included for completeness
                0x1 -> ImageFormat.RGBA_8888 // ImageFormat.RGB_565 or similar
                else -> ImageFormat.UNKNOWN
            }
        }

        /**
         * Copies a ByteBuffer to a ByteArray for safe access.
         *
         * This is necessary because ImageProxy buffers are only valid while the
         * ImageProxy is open. By copying the data, we ensure it remains accessible
         * even after the ImageProxy is closed.
         *
         * @param buffer The source ByteBuffer
         * @return ByteArray containing a copy of the buffer's data
         */
        private fun copyBuffer(buffer: ByteBuffer): ByteArray {
            buffer.rewind()
            val data = ByteArray(buffer.remaining())
            buffer.get(data)
            return data
        }
    }
