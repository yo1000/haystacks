package com.yo1000.haystacks.domain.repository

import com.yo1000.haystacks.domain.entity.Column
import com.yo1000.haystacks.domain.entity.Index
import com.yo1000.haystacks.domain.entity.Object
import com.yo1000.haystacks.domain.entity.Table
import com.yo1000.haystacks.domain.valueobject.TableName

/**
 * @author yo1000
 */
interface TableRepository {
    fun findTableObjects(vararg q: String): List<Object>
    fun findTable(name: TableName): Table
    fun findColumns(name: TableName): List<Column>
    fun findIndexes(name: TableName): List<Index>
    fun findRowSizeMap(name: TableName): Map<TableName, Long>
    fun findParentRelationCountMap(vararg query: TableName): Map<TableName, Int>
    fun findChildrenRelationCountMap(vararg query: TableName): Map<TableName, Int>
}
