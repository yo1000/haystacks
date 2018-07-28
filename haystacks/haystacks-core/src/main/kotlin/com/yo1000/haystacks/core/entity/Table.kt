package com.yo1000.haystacks.core.entity

/**
 * @author yo1000
 */
class Table(
        val names: TableNames,
        val columns: List<Column>,
        val rowCount: Long = 0
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Table

        if (names != other.names) return false
        if (columns != other.columns) return false
        if (rowCount != other.rowCount) return false

        return true
    }

    override fun hashCode(): Int {
        var result = names.hashCode()
        result = 31 * result + columns.hashCode()
        result = 31 * result + rowCount.hashCode()
        return result
    }
}
