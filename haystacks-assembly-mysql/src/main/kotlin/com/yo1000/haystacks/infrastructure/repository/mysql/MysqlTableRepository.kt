package com.yo1000.haystacks.infrastructure.repository.mysql

import com.yo1000.haystacks.domain.entity.*
import com.yo1000.haystacks.domain.repository.TableRepository
import com.yo1000.haystacks.domain.valueobject.ColumnPhysicalName
import com.yo1000.haystacks.domain.valueobject.LogicalName
import com.yo1000.haystacks.domain.valueobject.TablePhysicalName
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository

/**
 *
 * @author yo1000
 */
@Repository
class MysqlTableRepository(
        private val dataSourceProperties: DataSourceProperties,
        private val jdbcTemplate: NamedParameterJdbcTemplate
) : TableRepository {
    companion object {
        const val OUTPUT_TABLE_NAME = "table_name"
        const val OUTPUT_TABLE_COMMENT = "table_comment"
        const val OUTPUT_TABLE_ROWS = "table_rows"
        const val OUTPUT_COLUMN_NAME = "column_name"
        const val OUTPUT_COLUMN_COMMENT = "column_comment"
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

    override fun findNames(vararg q: String): FoundNamesMap {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findTableNamesAll(): List<TableNames> = jdbcTemplate.query("""
        SELECT
            tbl.table_name    AS $OUTPUT_TABLE_NAME,
            tbl.table_comment AS $OUTPUT_TABLE_COMMENT
        FROM
            information_schema.tables tbl
        WHERE
            tbl.table_schema = :$INPUT_SCHEMA_NAME
        AND
            tbl.table_type = 'BASE TABLE'
        """.trimIndent(), mapOf(
            INPUT_SCHEMA_NAME to dataSourceProperties.name
    )) { resultSet, _ ->
        TableNames(
                physicalName = TablePhysicalName(resultSet.getString(OUTPUT_TABLE_NAME)),
                logicalName = LogicalName(resultSet.getString(OUTPUT_TABLE_COMMENT))
        )
    }

    override fun findTable(name: TablePhysicalName): Table {
        data class TableItem(
                val name: String,
                val comment: String,
                val rows: Long
        )
        data class ColumnItem(
                val name: String,
                val comment: String,
                val type: String,
                val nullable: Boolean,
                val default: String?
        )
        data class RelationItem(
                val tableName: String,
                val columnName: String
        )
        data class ResultItem(
                val tableItem: TableItem,
                val columnItem: ColumnItem,
                val parentItem: RelationItem?,
                val childItem: RelationItem?
        )

        return jdbcTemplate.query("""
            SELECT
                tbl.table_name                AS $OUTPUT_TABLE_NAME,
                tbl.table_comment             AS $OUTPUT_TABLE_COMMENT,
                tbl.table_rows                AS $OUTPUT_TABLE_ROWS,
                col.column_name               AS $OUTPUT_COLUMN_NAME,
                col.column_comment            AS $OUTPUT_COLUMN_COMMENT,
                col.column_type               AS $OUTPUT_COLUMN_TYPE,
                col.is_nullable               AS $OUTPUT_COLUMN_NULLABLE,
                col.column_default            AS $OUTPUT_COLUMN_DEFAULT,
                parent.referenced_table_name  AS $OUTPUT_PARENT_TABLE_NAME,
                parent.referenced_column_name AS $OUTPUT_PARENT_COLUMN_NAME,
                child.table_name              AS $OUTPUT_CHILD_TABLE_NAME,
                child.column_name             AS $OUTPUT_CHILD_COLUMN_NAME
            FROM
                information_schema.tables tbl
            INNER JOIN
                information_schema.columns col
                ON  tbl.table_schema = col.table_schema
                AND tbl.table_name = col.table_name
            LEFT OUTER JOIN
                information_schema.key_column_usage parent
                ON  col.table_schema = parent.table_schema
                AND col.table_name = parent.table_name
                AND col.column_name = parent.column_name
            LEFT OUTER JOIN
                information_schema.key_column_usage child
                ON  col.table_schema = child.table_schema
                AND col.table_name = child.referenced_table_name
                AND col.column_name = child.referenced_column_name
            WHERE
                tbl.table_schema = :$INPUT_SCHEMA_NAME
            AND tbl.table_name = :$INPUT_TABLE_NAME
            AND tbl.table_type = 'BASE TABLE'
            ORDER BY
                col.ordinal_position
            """.trimIndent(), mapOf(
                INPUT_SCHEMA_NAME to dataSourceProperties.name,
                INPUT_TABLE_NAME to name.value
        )) { resultSet, _ ->
            val tableName = resultSet.getString(OUTPUT_TABLE_NAME)
            val tableComment = resultSet.getString(OUTPUT_TABLE_COMMENT)
            val tableRows = resultSet.getLong(OUTPUT_TABLE_ROWS)

            val columnName = resultSet.getString(OUTPUT_COLUMN_NAME)
            val columnComment = resultSet.getString(OUTPUT_COLUMN_COMMENT)
            val columnType = resultSet.getString(OUTPUT_COLUMN_TYPE)
            val columnNullable = resultSet.getString(OUTPUT_COLUMN_NULLABLE)
            val columnDefault = resultSet.getString(OUTPUT_COLUMN_DEFAULT)

            val parentTableName = resultSet.getString(OUTPUT_PARENT_TABLE_NAME)
            val parentColumnName = resultSet.getString(OUTPUT_PARENT_COLUMN_NAME)
            val childTableName = resultSet.getString(OUTPUT_CHILD_TABLE_NAME)
            val childColumnName = resultSet.getString(OUTPUT_CHILD_COLUMN_NAME)

            ResultItem(
                    tableItem = TableItem(
                            name = tableName,
                            comment = tableComment ?: "",
                            rows = tableRows
                    ),
                    columnItem = ColumnItem(
                            name = columnName,
                            comment = columnComment ?: "",
                            type = columnType,
                            nullable = columnNullable != "NO",
                            default = columnDefault
                    ),
                    parentItem = parentColumnName?.let {
                        RelationItem(
                                tableName = parentTableName,
                                columnName = parentColumnName
                        )
                    },
                    childItem = childColumnName?.let {
                        RelationItem(
                                tableName = childTableName,
                                columnName = childColumnName
                        )
                    }
            )
        }.groupBy({
            it.tableItem
        }, {
            Triple(it.columnItem, it.parentItem, it.childItem)
        }).map {
            Table(
                    names = TableNames(
                            physicalName = TablePhysicalName(it.key.name),
                            logicalName = LogicalName(it.key.comment)
                    ),
                    rowCount = it.key.rows,
                    columns = it.value.groupBy({
                        it.first
                    }, {
                        it.second to it.third
                    }).map {
                        Column(
                                names = ColumnNames(
                                        physicalName = ColumnPhysicalName(it.key.name),
                                        logicalName = LogicalName(it.key.comment)
                                ),
                                type = it.key.type,
                                nullable = it.key.nullable,
                                default = it.key.default,
                                parent = it.value.mapNotNull { it.first }.firstOrNull()?.let {
                                    Relation(
                                            tablePhysicalName = TablePhysicalName(it.tableName),
                                            columnPhysicalName = ColumnPhysicalName(it.columnName)
                                    )
                                },
                                children = it.value.mapNotNull { it.second }.map {
                                    Relation(
                                            tablePhysicalName = TablePhysicalName(it.tableName),
                                            columnPhysicalName = ColumnPhysicalName(it.columnName)
                                    )
                                }
                        )
                    }
            )
        }.first()
    }

    override fun findColumnCountMap(): Map<TablePhysicalName, Int> = jdbcTemplate.query("""
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
            INPUT_SCHEMA_NAME to dataSourceProperties.name
    )) { resultSet, _ ->
        TablePhysicalName(resultSet.getString(OUTPUT_TABLE_NAME)) to resultSet.getInt(OUTPUT_REFERENCE_COUNT)
    }.toMap()

    override fun findRowCountMap(): Map<TablePhysicalName, Long> = jdbcTemplate.query("""
        SELECT
            tbl.table_name  AS $OUTPUT_TABLE_NAME,
            tbl.table_rows  AS $OUTPUT_TABLE_ROWS
        FROM
            information_schema.tables tbl
        WHERE
            tbl.table_schema = :$INPUT_SCHEMA_NAME
        AND
            tbl.table_type = 'BASE TABLE'
        """.trimIndent(), mapOf(
            INPUT_SCHEMA_NAME to dataSourceProperties.name
    )) { resultSet, _ ->
        TablePhysicalName(resultSet.getString(OUTPUT_TABLE_NAME)) to resultSet.getLong(OUTPUT_TABLE_ROWS)
    }.toMap()

    override fun findReferencedCountFromChildrenMap(): Map<TablePhysicalName, Int> = jdbcTemplate.query("""
        SELECT
            table_name AS $OUTPUT_TABLE_NAME,
            sum(count) AS $OUTPUT_REFERENCE_COUNT
        FROM
            (
                SELECT
                    referenced_table_name   AS table_name,
                    count(column_name)      AS count
                FROM
                    information_schema.key_column_usage
                WHERE
                    table_schema = :$INPUT_SCHEMA_NAME
                    AND
                    referenced_table_name IS NOT NULL
                GROUP BY
                    referenced_table_name, table_name
            ) child_col
        GROUP BY
            table_name
        """.trimIndent(), mapOf(
            INPUT_SCHEMA_NAME to dataSourceProperties.name
    )) { resultSet, _ ->
        TablePhysicalName(resultSet.getString(OUTPUT_TABLE_NAME)) to resultSet.getInt(OUTPUT_REFERENCE_COUNT)
    }.toMap()

    override fun findReferencingCountToParentMap(): Map<TablePhysicalName, Int> = jdbcTemplate.query("""
        SELECT
            table_name      AS $OUTPUT_TABLE_NAME,
            sum(col_count)  AS $OUTPUT_REFERENCE_COUNT
        FROM
            (
                SELECT
                    table_name,
                    count(referenced_column_name) AS col_count
                FROM
                    information_schema.key_column_usage
                WHERE
                    table_schema = :$INPUT_SCHEMA_NAME
                GROUP BY
                    table_name, referenced_table_name
            ) parent_col
        GROUP BY
            table_name
        """.trimIndent(), mapOf(
            INPUT_SCHEMA_NAME to dataSourceProperties.name
    )) { resultSet, _ ->
        TablePhysicalName(resultSet.getString(OUTPUT_TABLE_NAME)) to resultSet.getInt(OUTPUT_REFERENCE_COUNT)
    }.toMap()
}
