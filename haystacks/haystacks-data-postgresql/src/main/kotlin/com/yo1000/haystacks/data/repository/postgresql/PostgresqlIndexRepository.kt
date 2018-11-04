package com.yo1000.haystacks.data.repository.postgresql

import com.yo1000.haystacks.core.entity.Index
import com.yo1000.haystacks.core.entity.IndexNames
import com.yo1000.haystacks.core.repository.IndexRepository
import com.yo1000.haystacks.core.valueobject.*
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
        const val OUTPUT_INDEX_TABLE_OID = "index_table_oid"
        const val OUTPUT_INDEX_COLUMN_IDS = "index_column_ids"
        const val OUTPUT_INDEXED_COLUMN_NAME = "indexed_column_name"

        const val INPUT_SCHEMA_NAME = "schemaName"
        const val INPUT_TABLE_NAME = "tableName"
        const val INPUT_TABLE_OID = "tableOid"
        const val INPUT_COLUMN_IDS = "columnIds"
    }

    override fun findByTableName(name: TablePhysicalName): List<Index> = jdbcOperations.query("""
        SELECT
            CASE
                WHEN als_idx.indisprimary = 't' THEN 'PRIMARY'
                WHEN als_idx.indisunique  = 't' THEN 'UNIQUE'
                ELSE                                 'PERFORMANCE'
            END                 AS $OUTPUT_INDEX_TYPE,
            als_cls_idx.relname AS $OUTPUT_INDEX_NAME,
            CONCAT(
                als_stt.schemaname, '.',
                als_stt.relname, '.',
                als_cls_idx.relname
            )                   AS $OUTPUT_INDEX_FQN,
            als_dsc.description AS $OUTPUT_INDEX_COMMENT,
            als_idx.indrelid    AS $OUTPUT_INDEX_TABLE_OID,
            als_idx.indkey      AS $OUTPUT_INDEX_COLUMN_IDS
        FROM
            pg_index als_idx
        INNER JOIN
            pg_class als_cls_idx
            ON  als_idx.indexrelid = als_cls_idx.oid
        INNER JOIN
            pg_class als_cls_tbl
            ON  als_idx.indrelid = als_cls_tbl.oid
        INNER JOIN
            pg_stat_user_tables als_stt
            ON  als_idx.indrelid = als_stt.relid
        LEFT OUTER JOIN
            pg_description als_dsc
            ON  als_idx.indexrelid = als_dsc.objoid
        WHERE
                als_stt.schemaname = :$INPUT_SCHEMA_NAME
            AND als_stt.relname = :$INPUT_TABLE_NAME
        """.trimIndent(), mapOf(
            INPUT_SCHEMA_NAME to schemaName,
            INPUT_TABLE_NAME to name.value
    )) { resultSet, _ ->
        val indexType = resultSet.getString(OUTPUT_INDEX_TYPE)
        val indexName = resultSet.getString(OUTPUT_INDEX_NAME)
        val indexFqn = resultSet.getString(OUTPUT_INDEX_FQN)
        val indexComment = resultSet.getString(OUTPUT_INDEX_COMMENT) ?: ""

        val indexTableOid = resultSet.getLong(OUTPUT_INDEX_TABLE_OID)
        val indexColumnIds = resultSet.getString(OUTPUT_INDEX_COLUMN_IDS).split(' ').map { it.toLong() }

        Index(
                names = IndexNames(
                        physicalName = IndexPhysicalName(indexName),
                        logicalName = LogicalName(indexComment),
                        fullyQualifiedName = FullyQualifiedName(indexFqn)
                ),
                type = Index.Type.valueOf(indexType),
                columnNames = jdbcOperations.query("""
                    SELECT
                        attname AS $OUTPUT_INDEXED_COLUMN_NAME
                    FROM
                        pg_attribute
                    WHERE
                            attrelid = :$INPUT_TABLE_OID
                        AND ( ${(1..indexColumnIds.size).map { """
                            attnum = :${INPUT_COLUMN_IDS}_$it
                            """ }.joinToString(separator = " OR ") } )
                    """.trimIndent(), (
                        (1..indexColumnIds.size).map { "${INPUT_COLUMN_IDS}_$it" to indexColumnIds[it - 1] } +
                                (INPUT_TABLE_OID to indexTableOid)).toMap()) { columnNameResultSet, _ ->
                    ColumnPhysicalName(columnNameResultSet.getString(OUTPUT_INDEXED_COLUMN_NAME))
                }
        )
    }
}
