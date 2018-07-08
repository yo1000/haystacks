package com.yo1000.haystacks.domain.repository

import com.yo1000.haystacks.domain.entity.Column
import com.yo1000.haystacks.domain.entity.FoundNamednessMap
import com.yo1000.haystacks.domain.entity.Index
import com.yo1000.haystacks.domain.entity.TableNamedness
import com.yo1000.haystacks.domain.valueobject.Statement
import com.yo1000.haystacks.domain.valueobject.TablePhysicalName

/**
 * @author yo1000
 */
interface TableRepository {
    fun findNamedness(vararg q: String): FoundNamednessMap
    fun findTableNamedness(name: TablePhysicalName): TableNamedness
    fun findColumns(name: TablePhysicalName): List<Column>
    fun findIndexes(name: TablePhysicalName): List<Index>
    fun findStatement(name: TablePhysicalName): Statement
    fun findColumnCountMap(): Map<TablePhysicalName, Int>
    fun findRowCountMap(): Map<TablePhysicalName, Long>
    fun findReferencedCountFromChildrenMap(): Map<TablePhysicalName, Int>
    fun findReferencingCountToParentMap(): Map<TablePhysicalName, Int>
}
