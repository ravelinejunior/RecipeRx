package br.com.raveline.reciperx.data.database.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converter {
    private val gson = Gson()

    @TypeConverter
    fun fromList(value: String?): List<String>? {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun stringToList(value: List<Any>): String {
        return gson.toJson(value)
    }

}