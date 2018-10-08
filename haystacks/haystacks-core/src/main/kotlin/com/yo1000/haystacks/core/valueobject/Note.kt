package com.yo1000.haystacks.core.valueobject

/**
 *
 * @author yo1000
 */
class Note(
        val value: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Note

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}
