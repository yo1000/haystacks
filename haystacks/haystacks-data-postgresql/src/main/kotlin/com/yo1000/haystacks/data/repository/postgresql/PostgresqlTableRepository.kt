package com.yo1000.haystacks.data.repository.postgresql

import com.yo1000.haystacks.core.entity.FoundNames
import com.yo1000.haystacks.core.entity.Table
import com.yo1000.haystacks.core.entity.TableNames
import com.yo1000.haystacks.core.repository.TableRepository
import com.yo1000.haystacks.core.valueobject.Statement
import com.yo1000.haystacks.core.valueobject.TablePhysicalName
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations

class PostgresqlTableRepository(
        private val jdbcOperations: NamedParameterJdbcOperations,
        private val schemaName: String
) : TableRepository {
    override fun findNames(vararg q: String): List<FoundNames> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findTableNamesAll(): List<TableNames> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findTable(name: TablePhysicalName): Table {
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

    override fun findStatementByName(name: TablePhysicalName): Statement {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
