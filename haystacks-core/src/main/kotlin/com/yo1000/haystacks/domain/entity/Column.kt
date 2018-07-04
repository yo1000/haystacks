package com.yo1000.haystacks.domain.entity

import com.yo1000.haystacks.domain.valueobject.ColumnName
import com.yo1000.haystacks.domain.valueobject.Comment

/**
 * @author yo1000
 */
class Column(
        name: ColumnName,
        comment: Comment,
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
        return getContainedIn(indices).any { it.type == Index.Type.PRIMARY }
    }

    fun indexesUnique(indices: Collection<Index>): Boolean {
        return getContainedIn(indices).any { it.type == Index.Type.UNIQUE }
    }

    fun indexesPerformance(indices: Collection<Index>): Boolean {
        return getContainedIn(indices).any { it.type == Index.Type.PERFORMANCE }
    }
}
