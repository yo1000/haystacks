package com.yo1000.haystacks.domain.valueobject

/**
 *
 * @author yo1000
 */
class TablePhysicalName(
        value: String
) : PhysicalName(value) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false
        return true
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}
