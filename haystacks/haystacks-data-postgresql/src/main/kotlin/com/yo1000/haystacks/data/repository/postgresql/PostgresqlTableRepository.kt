package com.yo1000.haystacks.data.repository.postgresql

import com.yo1000.haystacks.core.entity.FoundNames
import com.yo1000.haystacks.core.entity.Table
import com.yo1000.haystacks.core.entity.TableNames
import com.yo1000.haystacks.core.repository.TableRepository
import com.yo1000.haystacks.core.valueobject.FullyQualifiedName
import com.yo1000.haystacks.core.valueobject.LogicalName
import com.yo1000.haystacks.core.valueobject.Statement
import com.yo1000.haystacks.core.valueobject.TablePhysicalName
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations

class PostgresqlTableRepository(
        private val jdbcOperations: NamedParameterJdbcOperations,
        private val schemaName: String
) : TableRepository {
    companion object {
        const val OUTPUT_TABLE_NAME = "table_name"
        const val OUTPUT_TABLE_COMMENT = "table_comment"
        const val OUTPUT_TABLE_FQN = "table_fqn"
        const val OUTPUT_TABLE_ROWS = "table_rows"

        const val OUTPUT_COLUMN_NAME = "column_name"
        const val OUTPUT_COLUMN_COMMENT = "column_comment"
        const val OUTPUT_COLUMN_FQN = "column_fqn"
        const val OUTPUT_COLUMN_TYPE = "column_type"
        const val OUTPUT_COLUMN_DEFAULT = "column_default"
        const val OUTPUT_COLUMN_NULLABLE = "column_nullable"

        const val OUTPUT_PARENT_TABLE_NAME = "parent_table_name"
        const val OUTPUT_PARENT_COLUMN_NAME = "parent_column_name"
        const val OUTPUT_CHILD_TABLE_NAME = "child_table_name"
        const val OUTPUT_CHILD_COLUMN_NAME = "child_column_name"
        const val OUTPUT_REFERENCE_COUNT = "reference_count"

        const val INPUT_SCHEMA_NAME = "schemaName"
        const val INPUT_TABLE_NAME = "tableName"
    }

    override fun findNames(vararg q: String): List<FoundNames> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findTableNamesAll(): List<TableNames> = jdbcOperations.query("""
        SELECT
            tbl.table_name    AS $OUTPUT_TABLE_NAME,
            dsc.description   AS $OUTPUT_TABLE_COMMENT,
            CONCAT(
                tbl.table_schema, '.',
                tbl.table_name
            )                 AS $OUTPUT_TABLE_FQN
        FROM
            information_schema.tables tbl
        LEFT OUTER JOIN
            pg_description dsc
            ON CONCAT(
                tbl.table_schema, '.',
                tbl.table_name
            )::regclass = dsc.objoid
        WHERE
            tbl.table_schema = :$INPUT_SCHEMA_NAME
        AND tbl.table_type = 'BASE TABLE'
        ORDER BY
            tbl.table_name
        """.trimIndent(), mapOf(
            INPUT_SCHEMA_NAME to schemaName
    )) { resultSet, _ ->
        TableNames(
                physicalName = TablePhysicalName(resultSet.getString(OUTPUT_TABLE_NAME)),
                logicalName = LogicalName(resultSet.getString(OUTPUT_TABLE_COMMENT)),
                fullyQualifiedName = FullyQualifiedName(resultSet.getString(OUTPUT_TABLE_FQN))
        )
    }

    override fun findTable(name: TablePhysicalName): Table {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findColumnCountMap(): Map<TablePhysicalName, Int> = jdbcOperations.query("""
        SELECT
            tbl.table_name          AS $OUTPUT_TABLE_NAME,
            count(col.column_name)  AS $OUTPUT_REFERENCE_COUNT
        FROM
            information_schema.tables tbl
        INNER JOIN
            information_schema.columns col
                ON  tbl.table_schema  = col.table_schema
                AND tbl.table_name    = col.table_name
        WHERE
            tbl.table_schema  = :$INPUT_SCHEMA_NAME
        AND tbl.table_type    = 'BASE TABLE'
        GROUP BY
            tbl.table_name
        """.trimIndent(), mapOf(
            INPUT_SCHEMA_NAME to schemaName
    )) { resultSet, _ ->
        TablePhysicalName(resultSet.getString(OUTPUT_TABLE_NAME)) to resultSet.getInt(OUTPUT_REFERENCE_COUNT)
    }.toMap()

    override fun findRowCountMap(): Map<TablePhysicalName, Long> = jdbcOperations.query("""
        SELECT
            tbl.table_name  AS $OUTPUT_TABLE_NAME,
            stat.n_live_tup AS $OUTPUT_TABLE_ROWS
        FROM
            information_schema.tables tbl
        INNER JOIN
            pg_stat_user_tables stat
            ON  tbl.table_schema = stat.schemaname
            AND tbl.table_name = stat.relname
        WHERE
            tbl.table_schema = $INPUT_SCHEMA_NAME
        AND tbl.table_type = 'BASE TABLE'
        """.trimIndent(), mapOf(
            INPUT_SCHEMA_NAME to schemaName
    )) { resultSet, _ ->
        TablePhysicalName(resultSet.getString(OUTPUT_TABLE_NAME)) to resultSet.getLong(OUTPUT_TABLE_ROWS)
    }.toMap()

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
