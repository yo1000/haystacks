package com.yo1000.haystacks.core.entity

/**
 *
 * @author yo1000
 */
class FoundNames(
        val tableNames: TableNames,
        val columnNamesList: ColumnNamesList
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FoundNames

        if (tableNames != other.tableNames) return false
        if (columnNamesList != other.columnNamesList) return false

        return true
    }

    override fun hashCode(): Int {
        var result = tableNames.hashCode()
        result = 31 * result + columnNamesList.hashCode()
        return result
    }
}
