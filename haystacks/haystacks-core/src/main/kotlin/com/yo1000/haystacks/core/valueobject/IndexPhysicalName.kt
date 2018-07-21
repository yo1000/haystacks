package com.yo1000.haystacks.core.valueobject

/**
 *
 * @author yo1000
 */
class IndexPhysicalName(value: String) : PhysicalName(value) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}
