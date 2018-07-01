package com.yo1000.haystacks.domain.entity

/**
 * @author yo1000
 */
class Column(
        name: String,
        comment: String,
        val type: String,
        val nullable: Boolean,
        val defaultValue: String?,
        val children: List<Relation>,
        val parent: Relation
) : Object(name, comment) {
    fun getContainedIn(indices: Collection<Index>): List<Index> {
        return indices.filter {
            it.columns.any {
                it.name == this.name
            }
        }
    }

    fun indexesPrimary(indices: Collection<Index>): Boolean {
        return getContainedIn(indices).any { it.category == Index.Category.PRIMARY }
    }

    fun indexesUnique(indices: Collection<Index>): Boolean {
        return getContainedIn(indices).any { it.category == Index.Category.UNIQUE }
    }

    fun indexesPerformance(indices: Collection<Index>): Boolean {
        return getContainedIn(indices).any { it.category == Index.Category.PERFORMANCE }
    }
}
