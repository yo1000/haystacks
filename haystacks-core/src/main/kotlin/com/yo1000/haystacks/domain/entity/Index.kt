package com.yo1000.haystacks.domain.entity

import com.yo1000.haystacks.domain.valueobject.Comment
import com.yo1000.haystacks.domain.valueobject.IndexName

/**
 * @author yo1000
 */
class Index(
        name: IndexName,
        comment: Comment,
        val columns: List<Column>,
        val type: Type
) : Object(name, comment) {
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
