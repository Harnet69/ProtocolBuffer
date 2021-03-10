package com.harnet.protocolbuffer

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.createDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import java.io.IOException

class ProtoRepository(context: Context) {
    private val dataStore: DataStore<Person> = context.createDataStore(
            "my_data",
            serializer = MySerializer()
    )

    //initialize a dataStore(Choose Coroutines flow!!!)
    // use data store object to read a data
    val readProto: Flow<Person> = dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    Log.d("Error", exception.message.toString())
                    emit(Person.getDefaultInstance())
                } else {
                    throw exception
                }
            }

    //update one field inside Person
//    suspend fun updateValue(firstName: String) {
//        dataStore.updateData { preference ->
//            preference.toBuilder().setFirstName(firstName).build()
//        }
//    }

    //update Person's firstName
    suspend fun updatePersonFirstName(firstName: String) {
        dataStore.updateData { preference ->
            preference.toBuilder().setFirstName(firstName).build()
        }
    }

    //update Person's firstName
    suspend fun updatePersonLastName(lastName: String) {
        dataStore.updateData { preference ->
            preference.toBuilder().setLastName(lastName).build()
        }
    }

    //update Person's age
    suspend fun updatePersonAge(age: Int) {
        dataStore.updateData { preference ->
            preference.toBuilder().setAge(age).build()
        }
    }
}