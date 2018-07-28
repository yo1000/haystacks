package com.yo1000.haystacks.core.entity

import com.yo1000.haystacks.core.valueobject.LogicalName
import com.yo1000.haystacks.core.valueobject.PhysicalName


/**
 *
 * @author yo1000
 */
open class Names(
        val physicalName: PhysicalName,
        val logicalName: LogicalName
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Names

        if (physicalName != other.physicalName) return false
        if (logicalName != other.logicalName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = physicalName.hashCode()
        result = 31 * result + logicalName.hashCode()
        return result
    }
}
