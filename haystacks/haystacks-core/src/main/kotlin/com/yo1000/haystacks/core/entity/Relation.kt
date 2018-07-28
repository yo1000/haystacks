package com.yo1000.haystacks.core.entity

import com.yo1000.haystacks.core.valueobject.ColumnPhysicalName
import com.yo1000.haystacks.core.valueobject.TablePhysicalName

/**
 * @author yo1000
 */
class Relation(
        val tablePhysicalName: TablePhysicalName,
        val columnPhysicalName: ColumnPhysicalName
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Relation

        if (tablePhysicalName != other.tablePhysicalName) return false
        if (columnPhysicalName != other.columnPhysicalName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = tablePhysicalName.hashCode()
        result = 31 * result + columnPhysicalName.hashCode()
        return result
    }
}
