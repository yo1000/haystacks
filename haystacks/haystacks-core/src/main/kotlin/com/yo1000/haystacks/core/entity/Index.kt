package com.yo1000.haystacks.core.entity

import com.yo1000.haystacks.core.valueobject.ColumnPhysicalName

/**
 * @author yo1000
 */
class Index(
        val names: IndexNames,
        val columnNames: List<ColumnPhysicalName>,
        val type: Type
) {
    enum class Type(val order: Int) {
        NONE(0),
        PRIMARY(10),
        UNIQUE(20),
        PERFORMANCE(30)
    }

    fun getPrimary(): Boolean = type == Type.PRIMARY
    fun getUnique(): Boolean = type == Type.UNIQUE
    fun getPerformance(): Boolean = type == Type.PERFORMANCE

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Index

        if (names != other.names) return false
        if (columnNames != other.columnNames) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = names.hashCode()
        result = 31 * result + columnNames.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }
}
