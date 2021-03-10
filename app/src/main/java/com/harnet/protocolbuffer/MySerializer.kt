package com.harnet.protocolbuffer

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

// define how read and write an object to a data store
class MySerializer: Serializer<Person> {
    //it will be used if there is no file created yet
    override val defaultValue: Person
        get() = Person.getDefaultInstance()

    override fun readFrom(input: InputStream): Person {
        try {
            return Person.parseFrom(input)
        }catch (e: InvalidProtocolBufferException){
            throw CorruptionException("Cannot read .proto", e)
        }
    }

    override fun writeTo(t: Person, output: OutputStream) {
        t.writeTo(output)
    }
}