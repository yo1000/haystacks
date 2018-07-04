package com.yo1000.haystacks.domain.entity

import com.yo1000.haystacks.domain.valueobject.Comment
import com.yo1000.haystacks.domain.valueobject.TableName

/**
 * @author yo1000
 */
class Table(
        name: TableName,
        comment: Comment,
        val columns: List<Column>,
        val rowSize: Long = 0,
        val referencedCount: Long = 0
) : Object(name, comment)
