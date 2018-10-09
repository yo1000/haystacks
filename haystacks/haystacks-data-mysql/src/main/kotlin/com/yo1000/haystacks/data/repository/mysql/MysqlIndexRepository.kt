package com.yo1000.haystacks.data.repository.mysql

import com.yo1000.haystacks.core.Quadruple
import com.yo1000.haystacks.core.entity.Index
import com.yo1000.haystacks.core.entity.IndexNames
import com.yo1000.haystacks.core.repository.IndexRepository
import com.yo1000.haystacks.core.valueobject.*
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations

/**
 * @author yo1000
 */
class MysqlIndexRepository(
        private val jdbcOperations: NamedParameterJdbcOperations,
        private val dataSourceName: String
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

    override fun findByTableName(name: TablePhysicalName): List<Index> {
        data class IndexItem(
                val name: String,
                val comment: String,
                val fqn: String,
                val type: String,
                val columnName: String
        )

        return jdbcOperations.query("""
            SELECT
                idx.index_name  AS $OUTPUT_INDEX_NAME,
                idx.comment     AS $OUTPUT_INDEX_COMMENT,
                CONCAT(
                    idx.table_schema, '.',
                    idx.table_name  , '.',
                    idx.index_name
                )               AS $OUTPUT_INDEX_FQN,
                CASE
                    WHEN idx.index_name = 'PRIMARY' THEN  'PRIMARY'
                    WHEN idx.non_unique = 0         THEN  'UNIQUE'
                    ELSE                                  'PERFORMANCE'
                END             AS $OUTPUT_INDEX_TYPE,
                idx.column_name AS $OUTPUT_INDEXED_COLUMN_NAME
            FROM
                information_schema.statistics idx
            WHERE
                idx.table_schema    = :$INPUT_SCHEMA_NAME
            AND idx.table_name      = :$INPUT_TABLE_NAME
            ORDER BY
                CASE
                    WHEN idx.index_name = 'PRIMARY' THEN  1
                    WHEN idx.non_unique = 0         THEN  2
                    ELSE                                  3
                END,
                idx.seq_in_index
        """.trimIndent(), mapOf(
                INPUT_SCHEMA_NAME to dataSourceName,
                INPUT_TABLE_NAME to name.value
        )) { resultSet, _ ->
            IndexItem(
                    name = resultSet.getString(OUTPUT_INDEX_NAME),
                    comment = resultSet.getString(OUTPUT_INDEX_COMMENT),
                    fqn = resultSet.getString(OUTPUT_INDEX_FQN),
                    type = resultSet.getString(OUTPUT_INDEX_TYPE),
                    columnName = resultSet.getString(OUTPUT_INDEXED_COLUMN_NAME)
            )
        }.groupBy({
            Quadruple(it.name, it.comment, it.fqn, it.type)
        }, {
            it.columnName
        }).map {
            Index(
                    names = IndexNames(
                            IndexPhysicalName(it.key.first),
                            LogicalName(it.key.second),
                            FullyQualifiedName(it.key.third)
                    ),
                    type = Index.Type.valueOf(it.key.fourth),
                    columnNames = it.value.map {
                        ColumnPhysicalName(it)
                    }
            )
        }
    }
}