package com.yo1000.haystacks.domain.entity

/**
 * @author yo1000
 */
class Table(
        name: String,
        comment: String,
        val columns: List<Column>,
        val rowSize: Long = 0,
        val referencedCount: Long = 0
) : Object(name, comment)
