package com.yo1000.haystacks.core.entity

import com.yo1000.haystacks.core.valueobject.ColumnPhysicalName
import com.yo1000.haystacks.core.valueobject.FullyQualifiedName
import com.yo1000.haystacks.core.valueobject.LogicalName

/**
 * @author yo1000
 */
class ColumnNames(
        physicalName: ColumnPhysicalName,
        logicalName: LogicalName,
        fullyQualifiedName: FullyQualifiedName
) : Names(
        physicalName,
        logicalName,
        fullyQualifiedName
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false
        return true
    }
}
