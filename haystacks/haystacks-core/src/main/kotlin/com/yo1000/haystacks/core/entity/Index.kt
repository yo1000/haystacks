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
}
