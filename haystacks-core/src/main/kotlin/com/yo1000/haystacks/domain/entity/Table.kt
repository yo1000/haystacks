package com.yo1000.haystacks.domain.entity

/**
 * @author yo1000
 */
class Table(
        val names: TableNames,
        val columns: List<Column>,
        val rowCount: Long = 0
)
