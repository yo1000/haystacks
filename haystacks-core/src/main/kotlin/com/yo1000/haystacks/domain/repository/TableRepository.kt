package com.yo1000.haystacks.domain.repository

import com.yo1000.haystacks.domain.entity.FoundNamesMap
import com.yo1000.haystacks.domain.entity.Table
import com.yo1000.haystacks.domain.entity.TableNames
import com.yo1000.haystacks.domain.valueobject.Statement
import com.yo1000.haystacks.domain.valueobject.TablePhysicalName

/**
 * @author yo1000
 */
interface TableRepository {
    fun findNames(vararg q: String): FoundNamesMap
    fun findTableNamesAll(): List<TableNames>
    fun findTable(name: TablePhysicalName): Table
    fun findColumnCountMap(): Map<TablePhysicalName, Int>
    fun findRowCountMap(): Map<TablePhysicalName, Long>
    fun findReferencedCountFromChildrenMap(): Map<TablePhysicalName, Int>
    fun findReferencingCountToParentMap(): Map<TablePhysicalName, Int>
    fun findStatementByName(name: TablePhysicalName): Statement
}
