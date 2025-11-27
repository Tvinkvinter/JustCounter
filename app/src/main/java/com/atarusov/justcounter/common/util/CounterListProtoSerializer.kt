package com.atarusov.justcounter.common.util

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.atarusov.justcounter.CounterListProto
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object CounterListProtoSerializer : Serializer<CounterListProto> {
    override val defaultValue: CounterListProto = CounterListProto.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): CounterListProto =
        try {
            CounterListProto.parseFrom(input)
        } catch (e: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", e)
        }

    override suspend fun writeTo(t: CounterListProto, output: OutputStream) =
        t.writeTo(output)
}