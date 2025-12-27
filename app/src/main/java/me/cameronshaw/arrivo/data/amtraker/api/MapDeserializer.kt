package me.cameronshaw.arrivo.data.amtraker.api

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

/**
 * A generic deserializer for API responses that can be either a Map<String, List<T>>
 * or an empty array `[]` in the case of a "not found" or empty result.
 *
 * @param T The type of the DTO object inside the list.
 * @property valueType The class of T, needed to overcome type erasure at runtime.
 */
class MapOrEmptyArrayDeserializer<T>(private val valueType: Class<T>) :
    JsonDeserializer<Map<String, List<T>>> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Map<String, List<T>> {
        // If the server returns an empty array `[]`, we interpret it as an empty map.
        if (json?.isJsonArray == true) {
            return emptyMap()
        }

        // Otherwise, the response is a JSON object, so we build the correct
        // parameterized type for Map<String, List<T>> and let Gson parse it.
        val listType = TypeToken.getParameterized(List::class.java, valueType).type
        val mapType = TypeToken.getParameterized(Map::class.java, String::class.java, listType).type

        return context!!.deserialize(json, mapType)
    }
}