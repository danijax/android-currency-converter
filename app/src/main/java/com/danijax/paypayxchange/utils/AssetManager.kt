package com.danijax.paypayxchange.utils

import android.content.Context
import androidx.annotation.Nullable
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset

/**
 * Utility to read Json from assets to enhance unit tests and create test data
 */
class AssetManager(private val context: Context? = null) {
    fun getJsonFromAssets(fileName: String): String? {
        val jsonString: String = try {
            val inputStream = context?.assets?.open(fileName)
            val size = inputStream?.available()
            val buffer = ByteArray(size?: 0)
            inputStream?.read(buffer)
            inputStream?.close()
            String(buffer, Charset.defaultCharset())
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
        return jsonString
    }

    fun loadString(@Nullable inputStream: InputStream?): String? {
        if (inputStream == null) {
            return null
        }
        try {
            ByteArrayOutputStream().use { result ->
                val buffer = ByteArray(4096)
                var length: Int
                while (inputStream.read(buffer).also { length = it } > 0) {
                    result.write(buffer, 0, length)
                }
                return result.toString("UTF-8")
            }
        } catch (e: IOException) {
            return null
        }
    }

    @Nullable
    fun loadString(@Nullable loader: ClassLoader?, name: String): String? {
        if (loader == null) {
            return null
        }
        try {
            loader.getResourceAsStream(name).use { inputStream -> return loadString(inputStream) }
        } catch (e: IOException) {
            println(e.message)
            return null
        } catch (e: NullPointerException) {
            println(e.message)
            return null
        }
    }

    @Nullable
    fun loadString(name: String): String? {
        return loadString(this::class.java.classLoader, name)
    }

}
