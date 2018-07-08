package com.yo1000.haystacks.domain.entity

import com.yo1000.haystacks.domain.valueobject.Statement

/**
 * @author yo1000
 */
class Table(
        val names: TableNames,
        val columns: List<Column>,
        val rowCount: Long = 0,
        val indexes: List<Index>,
        val statement: Statement
)
