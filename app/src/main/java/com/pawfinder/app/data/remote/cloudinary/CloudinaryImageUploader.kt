package com.pawfinder.app.data.remote.cloudinary

import android.content.Context
import android.net.Uri
import com.cloudinary.android.MediaManager
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class CloudinaryImageUploader(
    private val context: Context
) {

    suspend fun uploadImage(uri: Uri): String {
        return suspendCancellableCoroutine { continuation ->
            val requestId = MediaManager.get().upload(uri)
                .unsigned("ml_default")
                .callback(object : com.cloudinary.android.callback.UploadCallback {
                    override fun onStart(requestId: String?) = Unit

                    override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) = Unit

                    override fun onSuccess(
                        requestId: String?,
                        resultData: MutableMap<Any?, Any?>?
                    ) {
                        val uploadedUrl = resultData?.get("secure_url")?.toString()
                        if (uploadedUrl != null) {
                            continuation.resume(uploadedUrl)
                        } else {
                            continuation.resumeWithException(
                                IllegalStateException("Cloudinary upload succeeded but URL is missing")
                            )
                        }
                    }

                    override fun onError(
                        requestId: String?,
                        error: com.cloudinary.android.callback.ErrorInfo?
                    ) {
                        continuation.resumeWithException(
                            IllegalStateException(error?.description ?: "Cloudinary upload failed")
                        )
                    }

                    override fun onReschedule(
                        requestId: String?,
                        error: com.cloudinary.android.callback.ErrorInfo?
                    ) = Unit
                })
                .dispatch()

            continuation.invokeOnCancellation {
                MediaManager.get().cancelRequest(requestId)
            }
        }
    }
}