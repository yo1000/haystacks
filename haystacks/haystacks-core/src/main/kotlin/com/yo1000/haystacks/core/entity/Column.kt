package com.yo1000.haystacks.core.entity

/**
 * @author yo1000
 */
class Column(
        val names: ColumnNames,
        val type: String,
        val nullable: Boolean,
        val default: String?,
        val children: List<Relation>,
        val parent: Relation?
) {
    fun getContainedIn(indices: Collection<Index>): List<Index> {
        return indices.filter {
            it.columnNames.any {
                it.value == names.physicalName.value
            }
        }
    }

    fun indexesPrimary(indices: Collection<Index>): Boolean {
        return getContainedIn(indices).any { it.type == Index.Type.PRIMARY }
    }

    fun indexesUnique(indices: Collection<Index>): Boolean {
        return getContainedIn(indices).any { it.type == Index.Type.UNIQUE }
    }

    fun indexesPerformance(indices: Collection<Index>): Boolean {
        return getContainedIn(indices).any { it.type == Index.Type.PERFORMANCE }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Column

        if (names != other.names) return false
        if (type != other.type) return false
        if (nullable != other.nullable) return false
        if (default != other.default) return false
        if (children != other.children) return false
        if (parent != other.parent) return false

        return true
    }

    override fun hashCode(): Int {
        var result = names.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + nullable.hashCode()
        result = 31 * result + (default?.hashCode() ?: 0)
        result = 31 * result + children.hashCode()
        result = 31 * result + (parent?.hashCode() ?: 0)
        return result
    }
}
