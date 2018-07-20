package com.yo1000.haystacks.infrastructure.repository.mysql

import com.yo1000.haystacks.domain.entity.Index
import com.yo1000.haystacks.domain.entity.IndexNames
import com.yo1000.haystacks.domain.repository.IndexRepository
import com.yo1000.haystacks.domain.valueobject.ColumnPhysicalName
import com.yo1000.haystacks.domain.valueobject.IndexPhysicalName
import com.yo1000.haystacks.domain.valueobject.LogicalName
import com.yo1000.haystacks.domain.valueobject.TablePhysicalName
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository

/**
 * @author yo1000
 */
@Repository
class MysqlIndexRepository(
        private val dataSourceProperties: DataSourceProperties,
        private val jdbcTemplate: NamedParameterJdbcTemplate
) : IndexRepository {
    companion object {
        const val OUTPUT_INDEX_NAME = "index_name"
        const val OUTPUT_TABLE_COMMENT = "index_comment"
        const val OUTPUT_INDEX_TYPE = "index_type"
        const val OUTPUT_INDEXED_COLUMN_NAME = "indexed_column_name"

        const val INPUT_SCHEMA_NAME = "schemaName"
        const val INPUT_TABLE_NAME = "tableName"
    }

    override fun findByTableName(name: TablePhysicalName): List<Index> {
        data class IndexItem(
                val name: String,
                val comment: String,
                val type: String,
                val columnName: String
        )

        return jdbcTemplate.query("""
            SELECT
                idx.index_name  AS $OUTPUT_INDEX_NAME,
                idx.comment     AS $OUTPUT_TABLE_COMMENT,
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
                idx.seq_in_index
        """.trimIndent(), mapOf(
                INPUT_SCHEMA_NAME to dataSourceProperties.name,
                INPUT_TABLE_NAME to name.value
        )) { resultSet, _ ->
            IndexItem(
                    name = resultSet.getString(OUTPUT_INDEX_NAME),
                    comment = resultSet.getString(OUTPUT_TABLE_COMMENT),
                    type = resultSet.getString(OUTPUT_INDEX_TYPE),
                    columnName = resultSet.getString(OUTPUT_INDEXED_COLUMN_NAME)
            )
        }.groupBy({
            Triple(it.name, it.comment, it.type)
        }, {
            it.columnName
        }).map {
            Index(
                    names = IndexNames(
                            IndexPhysicalName(it.key.first),
                            LogicalName(it.key.second)
                    ),
                    type = Index.Type.valueOf(it.key.third),
                    columnNames = it.value.map {
                        ColumnPhysicalName(it)
                    }
            )
        }
    }
}