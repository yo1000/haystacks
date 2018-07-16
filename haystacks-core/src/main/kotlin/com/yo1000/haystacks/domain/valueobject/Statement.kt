package com.yo1000.haystacks.domain.valueobject

/**
 *
 * @author yo1000
 */
class Statement(
        val value: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Statement

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}
