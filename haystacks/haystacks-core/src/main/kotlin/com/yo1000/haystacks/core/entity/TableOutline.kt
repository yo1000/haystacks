package com.yo1000.haystacks.core.entity

/**
 *
 * @author yo1000
 */
class TableOutline(
        val names: TableNames,
        val columnCount: Int,
        val rowCount: Long,
        val referencedCountFromChildren: Int,
        val referencingCountToParent: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TableOutline

        if (names != other.names) return false
        if (columnCount != other.columnCount) return false
        if (rowCount != other.rowCount) return false
        if (referencedCountFromChildren != other.referencedCountFromChildren) return false
        if (referencingCountToParent != other.referencingCountToParent) return false

        return true
    }

    override fun hashCode(): Int {
        var result = names.hashCode()
        result = 31 * result + columnCount
        result = 31 * result + rowCount.hashCode()
        result = 31 * result + referencedCountFromChildren
        result = 31 * result + referencingCountToParent
        return result
    }
}
