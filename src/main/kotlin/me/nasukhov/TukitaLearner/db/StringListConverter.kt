package me.nasukhov.TukitaLearner.db

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class StringListConverter : AttributeConverter<List<String>, String> {
    companion object {
        private const val SEPARATOR = ";"
    }

    override fun convertToDatabaseColumn(list: List<String>?): String? {
        if (list == null || list.isEmpty()) {
            return null
        }

        return list.joinToString(SEPARATOR)
    }

    override fun convertToEntityAttribute(dbData: String?): MutableList<String> {
        if (dbData == null || dbData.isEmpty()) {
            return mutableListOf()
        }

        return dbData.split(SEPARATOR.toRegex()).dropLastWhile { it.isEmpty() }.toMutableList()
    }
}
