package com.example.fieldteam.data.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import java.io.InputStream
import java.io.OutputStream

@OptIn(ExperimentalSerializationApi::class)
object FcmTokenSerializer : Serializer<FcmTokenData> {

    private val protoBuf = ProtoBuf {
        encodeDefaults = true
    }

    override val defaultValue: FcmTokenData = FcmTokenData()

    override suspend fun readFrom(input: InputStream): FcmTokenData {
        return try {
            val bytes = input.readBytes()
            if (bytes.isEmpty()) {
                defaultValue
            } else {
                protoBuf.decodeFromByteArray(FcmTokenData.serializer(), bytes)
            }
        } catch (exception: Exception) {
            throw CorruptionException("Cannot read FcmTokenData protobuf", exception)
        }
    }

    override suspend fun writeTo(t: FcmTokenData, output: OutputStream) {
        val bytes = protoBuf.encodeToByteArray(FcmTokenData.serializer(), t)
        output.write(bytes)
    }
}
