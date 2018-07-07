package com.yo1000.haystacks.domain.entity

import com.yo1000.haystacks.domain.valueobject.Comment
import com.yo1000.haystacks.domain.valueobject.Statement
import com.yo1000.haystacks.domain.valueobject.TableName

/**
 * @author yo1000
 */
class Table(
        name: TableName,
        comment: Comment,
        val columns: List<Column>,
        val rowCount: Long = 0,
        val indexes: List<Index>,
        val statement: Statement
) : Object(name, comment)
