package com.yo1000.haystacks.core.repository

import com.yo1000.haystacks.core.entity.FoundNames
import com.yo1000.haystacks.core.entity.Table
import com.yo1000.haystacks.core.entity.TableNames
import com.yo1000.haystacks.core.valueobject.Statement
import com.yo1000.haystacks.core.valueobject.TablePhysicalName

/**
 * @author yo1000
 */
interface TableRepository {
    fun findNames(vararg q: String): List<FoundNames>
    fun findTableNamesAll(): List<TableNames>
    fun findTable(name: TablePhysicalName): Table
    fun findColumnCountMap(): Map<TablePhysicalName, Int>
    fun findRowCountMap(): Map<TablePhysicalName, Long>
    fun findReferencedCountFromChildrenMap(): Map<TablePhysicalName, Int>
    fun findReferencingCountToParentMap(): Map<TablePhysicalName, Int>
    fun findStatementByName(name: TablePhysicalName): Statement
}
