package com.yo1000.haystacks.domain.repository

import com.yo1000.haystacks.domain.entity.Index
import com.yo1000.haystacks.domain.entity.Table
import com.yo1000.haystacks.domain.valueobject.ColumnName
import com.yo1000.haystacks.domain.valueobject.TableName

/**
 * @author yo1000
 */
interface TableRepository {
    fun findTables(vararg query: TableName): List<Table>
    fun findTable(name: TableName): List<Table>
    fun findIndexes(name: TableName): List<Index>
    fun findParentRelationCounts(vararg query: TableName): Map<ColumnName, Int>
    fun findChildrenRelationCounts(vararg query: TableName): Map<ColumnName, Int>
    fun showStatement(name: TableName): String
}
