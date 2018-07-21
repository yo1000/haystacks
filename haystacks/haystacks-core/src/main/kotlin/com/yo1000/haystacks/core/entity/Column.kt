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
}
