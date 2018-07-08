package com.yo1000.haystacks.domain.entity

/**
 *
 * @author yo1000
 */
class TableOutline(
        val names: TableNames,
        val columnCount: Int,
        val rowCount: Long,
        val referencedCountFromChildren: Int,
        val referencingCountToParent: Int
)
