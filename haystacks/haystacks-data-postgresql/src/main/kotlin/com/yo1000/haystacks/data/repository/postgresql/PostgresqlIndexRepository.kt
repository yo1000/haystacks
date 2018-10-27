package com.yo1000.haystacks.data.repository.postgresql

import com.yo1000.haystacks.core.entity.Index
import com.yo1000.haystacks.core.repository.IndexRepository
import com.yo1000.haystacks.core.valueobject.TablePhysicalName
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations

class PostgresqlIndexRepository(
        private val jdbcOperations: NamedParameterJdbcOperations,
        private val schemaName: String
) : IndexRepository {
    override fun findByTableName(name: TablePhysicalName): List<Index> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
