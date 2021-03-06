package com.yo1000.haystacks.data.repository.mysql

import com.yo1000.haystacks.core.entity.*
import com.yo1000.haystacks.core.repository.TableRepository
import com.yo1000.haystacks.core.valueobject.*
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations

/**
 *
 * @author yo1000
 */
class MysqlTableRepository(
        private val jdbcOperations: NamedParameterJdbcOperations,
        private val dataSourceName: String
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

    override fun findNames(vararg q: String): List<FoundNames>  = jdbcOperations.query("""
        SELECT DISTINCT
            tbl.table_name      AS $OUTPUT_TABLE_NAME,
            tbl.table_comment   AS $OUTPUT_TABLE_COMMENT,
            CONCAT(
                tbl.table_schema, '.',
                tbl.table_name
            )                   AS $OUTPUT_TABLE_FQN,
            col.column_name     AS $OUTPUT_COLUMN_NAME,
            col.column_comment  AS $OUTPUT_COLUMN_COMMENT,
            CONCAT(
                tbl.table_schema, '.',
                tbl.table_name  , '.',
                col.column_name
            )                   AS $OUTPUT_COLUMN_FQN
        FROM
            (
                SELECT
                    tbl_1.table_name,
                    tbl_1.table_comment,
                    tbl_1.table_schema
                FROM
                    information_schema.tables tbl_1
                INNER JOIN
                    information_schema.columns col_1
                    ON  tbl_1.table_schema  = col_1.table_schema
                    AND tbl_1.table_name    = col_1.table_name
                WHERE
                    tbl_1.table_type = 'BASE TABLE'
                AND tbl_1.table_schema  = :$INPUT_SCHEMA_NAME
                AND ( ${(1..q.size).map { """
                        tbl_1.table_name        LIKE CONCAT(CONCAT('%', :keyword_$it), '%')
                    OR  tbl_1.table_comment     LIKE CONCAT(CONCAT('%', :keyword_$it), '%')
                    OR  col_1.column_name       LIKE CONCAT(CONCAT('%', :keyword_$it), '%')
                    OR  col_1.column_comment    LIKE CONCAT(CONCAT('%', :keyword_$it), '%')
                    OR  CONCAT(
                            tbl_1.table_schema, '.',
                            tbl_1.table_name
                        ) = :keyword_$it
                    OR  CONCAT(
                            tbl_1.table_schema, '.',
                            tbl_1.table_name, '.',
                            col_1.column_name
                        ) = :keyword_$it
                    """ }.joinToString(separator = " OR ")} )
            ) tbl
        INNER JOIN
            information_schema.columns col
            ON  tbl.table_schema    = col.table_schema
            AND tbl.table_name      = col.table_name
        """.trimIndent(), (
            (1..q.size).map { "keyword_$it" to q[it - 1]
            } + (INPUT_SCHEMA_NAME to dataSourceName)).toMap()
    ) { resultSet, _ ->
        val tableName = resultSet.getString(OUTPUT_TABLE_NAME)
        val tableComment = resultSet.getString(OUTPUT_TABLE_COMMENT)
        val tableFqn = resultSet.getString(OUTPUT_TABLE_FQN)

        val columnName = resultSet.getString(OUTPUT_COLUMN_NAME)
        val columnComment = resultSet.getString(OUTPUT_COLUMN_COMMENT)
        val columnFqn = resultSet.getString(OUTPUT_COLUMN_FQN)

        TableNames(
                physicalName = TablePhysicalName(tableName),
                logicalName = LogicalName(tableComment),
                fullyQualifiedName = FullyQualifiedName(tableFqn)
        ) to ColumnNames(
                physicalName = ColumnPhysicalName(columnName),
                logicalName = LogicalName(columnComment),
                fullyQualifiedName = FullyQualifiedName(columnFqn)
        )
    }.groupBy({
        it.first
    }, {
        it.second
    }).map {
        FoundNames(
                tableNames = it.key,
                columnNamesList = ColumnNamesList(*it.value.toTypedArray())
        )
    }

    override fun findTableNamesAll(): List<TableNames> = jdbcOperations.query("""
        SELECT
            tbl.table_name    AS $OUTPUT_TABLE_NAME,
            tbl.table_comment AS $OUTPUT_TABLE_COMMENT,
            CONCAT(
                tbl.table_schema, '.',
                tbl.table_name
            )                 AS $OUTPUT_TABLE_FQN
        FROM
            information_schema.tables tbl
        WHERE
            tbl.table_schema = :$INPUT_SCHEMA_NAME
        AND tbl.table_type = 'BASE TABLE'
        ORDER BY
            tbl.table_name
        """.trimIndent(), mapOf(
            INPUT_SCHEMA_NAME to dataSourceName
    )) { resultSet, _ ->
        TableNames(
                physicalName = TablePhysicalName(resultSet.getString(OUTPUT_TABLE_NAME)),
                logicalName = LogicalName(resultSet.getString(OUTPUT_TABLE_COMMENT)),
                fullyQualifiedName = FullyQualifiedName(resultSet.getString(OUTPUT_TABLE_FQN))
        )
    }

    override fun findTable(name: TablePhysicalName): Table {
        data class TableItem(
                val name: String,
                val comment: String,
                val fqn: String,
                val rows: Long
        )
        data class ColumnItem(
                val name: String,
                val comment: String,
                val fqn: String,
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

        return jdbcOperations.query("""
            SELECT
                tbl.table_name                AS $OUTPUT_TABLE_NAME,
                tbl.table_comment             AS $OUTPUT_TABLE_COMMENT,
                CONCAT(
                    tbl.table_schema, '.',
                    tbl.table_name
                )                             AS $OUTPUT_TABLE_FQN,
                tbl.table_rows                AS $OUTPUT_TABLE_ROWS,
                col.column_name               AS $OUTPUT_COLUMN_NAME,
                col.column_comment            AS $OUTPUT_COLUMN_COMMENT,
                CONCAT(
                    tbl.table_schema, '.',
                    tbl.table_name  , '.',
                    col.column_name
                )                             AS $OUTPUT_COLUMN_FQN,
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
                INPUT_SCHEMA_NAME to dataSourceName,
                INPUT_TABLE_NAME to name.value
        )) { resultSet, _ ->
            val tableName = resultSet.getString(OUTPUT_TABLE_NAME)
            val tableComment = resultSet.getString(OUTPUT_TABLE_COMMENT)
            val tableFqn = resultSet.getString(OUTPUT_TABLE_FQN)
            val tableRows = resultSet.getLong(OUTPUT_TABLE_ROWS)

            val columnName = resultSet.getString(OUTPUT_COLUMN_NAME)
            val columnComment = resultSet.getString(OUTPUT_COLUMN_COMMENT)
            val columnFqn = resultSet.getString(OUTPUT_COLUMN_FQN)
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
                            fqn = tableFqn,
                            rows = tableRows
                    ),
                    columnItem = ColumnItem(
                            name = columnName,
                            comment = columnComment ?: "",
                            fqn = columnFqn,
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
                            logicalName = LogicalName(it.key.comment),
                            fullyQualifiedName = FullyQualifiedName(it.key.fqn)
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
                                        logicalName = LogicalName(it.key.comment),
                                        fullyQualifiedName = FullyQualifiedName(it.key.fqn)
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
                                }.distinct()
                        )
                    }
            )
        }.first()
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
            INPUT_SCHEMA_NAME to dataSourceName
    )) { resultSet, _ ->
        TablePhysicalName(resultSet.getString(OUTPUT_TABLE_NAME)) to resultSet.getInt(OUTPUT_REFERENCE_COUNT)
    }.toMap()

    override fun findRowCountMap(): Map<TablePhysicalName, Long> = jdbcOperations.query("""
        SELECT
            tbl.table_name  AS $OUTPUT_TABLE_NAME,
            tbl.table_rows  AS $OUTPUT_TABLE_ROWS
        FROM
            information_schema.tables tbl
        WHERE
            tbl.table_schema = :$INPUT_SCHEMA_NAME
        AND tbl.table_type = 'BASE TABLE'
        """.trimIndent(), mapOf(
            INPUT_SCHEMA_NAME to dataSourceName
    )) { resultSet, _ ->
        TablePhysicalName(resultSet.getString(OUTPUT_TABLE_NAME)) to resultSet.getLong(OUTPUT_TABLE_ROWS)
    }.toMap()

    override fun findReferencedCountFromChildrenMap(): Map<TablePhysicalName, Int> = jdbcOperations.query("""
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
                AND referenced_table_name IS NOT NULL
                GROUP BY
                    referenced_table_name, table_name
            ) child_col
        GROUP BY
            table_name
        """.trimIndent(), mapOf(
            INPUT_SCHEMA_NAME to dataSourceName
    )) { resultSet, _ ->
        TablePhysicalName(resultSet.getString(OUTPUT_TABLE_NAME)) to resultSet.getInt(OUTPUT_REFERENCE_COUNT)
    }.toMap()

    override fun findReferencingCountToParentMap(): Map<TablePhysicalName, Int> = jdbcOperations.query("""
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
            INPUT_SCHEMA_NAME to dataSourceName
    )) { resultSet, _ ->
        TablePhysicalName(resultSet.getString(OUTPUT_TABLE_NAME)) to resultSet.getInt(OUTPUT_REFERENCE_COUNT)
    }.filter { it.second > 0 }.toMap()

    override fun findStatementByName(name: TablePhysicalName): Statement = jdbcOperations.query("""
        SHOW CREATE TABLE `${name.value}`
        """.trimIndent()
    ) { resultSet, _ ->
        Statement(resultSet.getString("create table"))
    }.first()
}
