package com.yo1000.haystacks.infrastructure.repository.mysql

import com.yo1000.haystacks.domain.entity.Column
import com.yo1000.haystacks.domain.entity.FoundNamednessMap
import com.yo1000.haystacks.domain.entity.Index
import com.yo1000.haystacks.domain.entity.TableNamedness
import com.yo1000.haystacks.domain.repository.TableRepository
import com.yo1000.haystacks.domain.valueobject.Statement
import com.yo1000.haystacks.domain.valueobject.TablePhysicalName

/**
 *
 * @author yo1000
 */
class MysqlTableRepository : TableRepository {
    override fun findNamedness(vararg q: String): FoundNamednessMap {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findTableNamedness(name: TablePhysicalName): TableNamedness {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findColumns(name: TablePhysicalName): List<Column> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findIndexes(name: TablePhysicalName): List<Index> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findStatement(name: TablePhysicalName): Statement {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findColumnCountMap(): Map<TablePhysicalName, Int> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findRowCountMap(): Map<TablePhysicalName, Long> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findReferencedCountFromChildrenMap(): Map<TablePhysicalName, Int> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findReferencingCountToParentMap(): Map<TablePhysicalName, Int> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}