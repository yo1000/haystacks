package com.yo1000.haystacks.domain.entity

/**
 * @author yo1000
 */
class Column(
        val names: ColumnNames,
        val type: String,
        val nullable: Boolean,
        val defaultValue: String?,
        val children: List<Relation>,
        val parent: Relation
) {
    fun getContainedIn(indices: Collection<Index>): List<Index> {
        return indices.filter {
            it.columns.any {
                it.names.physicalName == this.names.physicalName
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
