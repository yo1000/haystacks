package com.yo1000.haystacks.domain.entity

import com.yo1000.haystacks.domain.valueobject.Comment
import com.yo1000.haystacks.domain.valueobject.TableName

/**
 *
 * @author yo1000
 */
class TableOutline(
        name: TableName,
        comment: Comment,
        val columnCount: Int,
        val rowCount: Long,
        val referencedCountFromChildren: Int,
        val referencingCountToParent: Int
) : Object(
        name,
        comment
)