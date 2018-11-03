package com.yo1000.haystacks.data.repository.postgresql

import com.yo1000.haystacks.core.entity.Index
import com.yo1000.haystacks.core.repository.IndexRepository
import com.yo1000.haystacks.core.valueobject.TablePhysicalName
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations

class PostgresqlIndexRepository(
        private val jdbcOperations: NamedParameterJdbcOperations,
        private val schemaName: String
) : IndexRepository {
    companion object {
        const val OUTPUT_INDEX_NAME = "index_name"
        const val OUTPUT_INDEX_COMMENT = "index_comment"
        const val OUTPUT_INDEX_FQN = "index_fqn"
        const val OUTPUT_INDEX_TYPE = "index_type"
        const val OUTPUT_INDEXED_COLUMN_NAME = "indexed_column_name"

        const val INPUT_SCHEMA_NAME = "schemaName"
        const val INPUT_TABLE_NAME = "tableName"
    }

    override fun findByTableName(name: TablePhysicalName): List<Index> = emptyList()
}
