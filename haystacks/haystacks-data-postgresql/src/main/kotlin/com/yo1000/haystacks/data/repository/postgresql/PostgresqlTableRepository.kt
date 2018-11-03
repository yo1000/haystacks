package com.yo1000.haystacks.data.repository.postgresql

import com.yo1000.haystacks.core.entity.*
import com.yo1000.haystacks.core.repository.TableRepository
import com.yo1000.haystacks.core.valueobject.*
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

    override fun findNames(vararg q: String): List<FoundNames> = jdbcOperations.query("""
        WITH
        qry_tbl_col AS (
            SELECT
                als_stt.relid               AS obj_oid,
                als_att.attnum              AS obj_subid,
                als_tbl.table_schema        AS schema_name,
                als_tbl.table_name          AS table_name,
                CONCAT(
                    als_tbl.table_schema, '.',
                    als_tbl.table_name
                )                           AS table_fqn,
                als_dsc_tbl.description     AS table_comment,
                als_stt.n_live_tup          AS table_rows,
                als_col.column_name         AS column_name,
                CONCAT(
                    als_tbl.table_schema, '.',
                    als_tbl.table_name, '.',
                    als_col.column_name
                )                           AS column_fqn,
                als_dsc_col.description     AS column_comment,
                als_col.column_default      AS column_default,
                als_col.is_nullable         AS column_nullable,
                als_col.data_type           AS column_type,
                als_col.ordinal_position    AS ordinal_position
            FROM
                information_schema.tables als_tbl
            INNER JOIN
                information_schema.columns als_col
                ON  als_tbl.table_schema = als_col.table_schema
                AND als_tbl.table_name   = als_col.table_name
            INNER JOIN
                pg_stat_user_tables als_stt
                ON  als_tbl.table_schema = als_stt.schemaname
                AND als_tbl.table_name   = als_stt.relname
            INNER JOIN
                pg_attribute als_att
                ON  als_stt.relid       = als_att.attrelid
                AND als_col.column_name = als_att.attname
            LEFT OUTER JOIN
                pg_description als_dsc_tbl
                ON  als_stt.relid = als_dsc_tbl.objoid
                AND als_dsc_tbl.objsubid = 0
            LEFT OUTER JOIN
                pg_description als_dsc_col
                ON  als_stt.relid  = als_dsc_col.objoid
                AND als_att.attnum = als_dsc_col.objsubid
            WHERE
                als_tbl.table_type = 'BASE TABLE'
            AND als_tbl.table_schema = :$INPUT_SCHEMA_NAME
            AND als_tbl.table_name = :$INPUT_TABLE_NAME
            ORDER BY
                als_stt.relid,
                als_col.ordinal_position
        ),
        qry_fnd_tbl AS (
            SELECT DISTINCT
                als_tbl.table_schema    AS schema_name,
                als_tbl.table_name      AS table_name,
                als_tbl.table_fqn       AS table_fqn
            FROM
                qry_tbl_col
            WHERE
                ( ${(1..q.size).map { """
                        tbl_1.table_name        LIKE CONCAT(CONCAT('%', :keyword_$it), '%')
                    OR  tbl_1.table_comment     LIKE CONCAT(CONCAT('%', :keyword_$it), '%')
                    OR  col_1.column_name       LIKE CONCAT(CONCAT('%', :keyword_$it), '%')
                    OR  col_1.column_comment    LIKE CONCAT(CONCAT('%', :keyword_$it), '%')
                    """ }.joinToString(separator = " OR ")} )
        )
        SELECT
            qry_tbl_col.table_name      AS $OUTPUT_TABLE_NAME,
            qry_tbl_col.table_comment   AS $OUTPUT_TABLE_COMMENT,
            qry_tbl_col.table_fqn       AS $OUTPUT_TABLE_FQN,
            qry_tbl_col.column_name     AS $OUTPUT_COLUMN_NAME,
            qry_tbl_col.column_comment  AS $OUTPUT_COLUMN_COMMENT,
            qry_tbl_col.column_fqn      AS $OUTPUT_COLUMN_FQN
        FROM
            qry_fnd_tbl
        INNER JOIN
            qry_tbl_col
            ON  tbl.table_schema    = col.table_schema
            AND tbl.table_name      = col.table_name
        """.trimIndent(), (
                (1..q.size).map { "keyword_$it" to q[it - 1]
                } + (INPUT_SCHEMA_NAME to schemaName)).toMap()
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
            als_tbl.table_name          AS $OUTPUT_TABLE_NAME,
            CONCAT(
                als_tbl.table_schema, '.',
                als_tbl.table_name
            )                           AS $OUTPUT_TABLE_FQN,
            als_dsc_tbl.description     AS $OUTPUT_TABLE_COMMENT,
            als_stt.n_live_tup          AS $OUTPUT_TABLE_ROWS
        FROM
            information_schema.tables als_tbl
        INNER JOIN
            pg_stat_user_tables als_stt
            ON  als_tbl.table_schema = als_stt.schemaname
            AND als_tbl.table_name   = als_stt.relname
        LEFT OUTER JOIN
            pg_description als_dsc_tbl
            ON  als_stt.relid = als_dsc_tbl.objoid
            AND als_dsc_tbl.objsubid = 0
        WHERE
                als_tbl.table_schema    = :$INPUT_SCHEMA_NAME
            AND als_tbl.table_type      = 'BASE TABLE'
        ORDER BY
            als_tbl.table_name
        """.trimIndent(), mapOf(
            INPUT_SCHEMA_NAME to schemaName
    )) { resultSet, _ ->
        TableNames(
                physicalName = TablePhysicalName(resultSet.getString(OUTPUT_TABLE_NAME)),
                logicalName = LogicalName(resultSet.getString(OUTPUT_TABLE_COMMENT) ?: ""),
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
            WITH
            qry_tbl_col AS (
                SELECT
                    als_stt.relid               AS obj_oid,
                    als_att.attnum              AS obj_subid,
                    als_tbl.table_schema        AS schema_name,
                    als_tbl.table_name          AS table_name,
                    CONCAT(
                        als_tbl.table_schema, '.',
                        als_tbl.table_name
                    )                           AS table_fqn,
                    als_dsc_tbl.description     AS table_comment,
                    als_stt.n_live_tup          AS table_rows,
                    als_col.column_name         AS column_name,
                    CONCAT(
                        als_tbl.table_schema, '.',
                        als_tbl.table_name, '.',
                        als_col.column_name
                    )                           AS column_fqn,
                    als_dsc_col.description     AS column_comment,
                    als_col.column_default      AS column_default,
                    als_col.is_nullable         AS column_nullable,
                    als_col.data_type           AS column_type,
                    als_col.ordinal_position    AS ordinal_position
                FROM
                    information_schema.tables als_tbl
                INNER JOIN
                    information_schema.columns als_col
                    ON  als_tbl.table_schema = als_col.table_schema
                    AND als_tbl.table_name   = als_col.table_name
                INNER JOIN
                    pg_stat_user_tables als_stt
                    ON  als_tbl.table_schema = als_stt.schemaname
                    AND als_tbl.table_name   = als_stt.relname
                INNER JOIN
                    pg_attribute als_att
                    ON  als_stt.relid       = als_att.attrelid
                    AND als_col.column_name = als_att.attname
                LEFT OUTER JOIN
                    pg_description als_dsc_tbl
                    ON  als_stt.relid = als_dsc_tbl.objoid
                    AND als_dsc_tbl.objsubid = 0
                LEFT OUTER JOIN
                    pg_description als_dsc_col
                    ON  als_stt.relid  = als_dsc_col.objoid
                    AND als_att.attnum = als_dsc_col.objsubid
                WHERE
                    als_tbl.table_type = 'BASE TABLE'
                AND als_tbl.table_schema = :$INPUT_SCHEMA_NAME
                AND als_tbl.table_name = :$INPUT_TABLE_NAME
                ORDER BY
                    als_stt.relid,
                    als_col.ordinal_position
            ),
            qry_rel AS (
                SELECT
                    als_cns_tbl.table_schema    AS schema_name,
                    als_cns_tbl.table_name      AS child_table_name,
                    als_key_col_usg.column_name AS child_column_name,
                    CONCAT(
                        als_cns_tbl.table_schema, '.',
                        als_cns_tbl.table_name, '.',
                        als_key_col_usg.column_name
                    )                           AS child_column_fqn,
                    als_cns_col_usg.table_name  AS parent_table_name,
                    als_cns_col_usg.column_name AS parent_column_name,
                    CONCAT(
                        als_cns_tbl.table_schema, '.',
                        als_cns_col_usg.table_name, '.',
                        als_cns_col_usg.column_name
                    )                           AS parent_column_fqn
                FROM
                    information_schema.table_constraints als_cns_tbl
                JOIN
                    information_schema.key_column_usage als_key_col_usg
                    ON  als_cns_tbl.constraint_name = als_key_col_usg.constraint_name
                    AND als_cns_tbl.table_schema = als_key_col_usg.table_schema
                JOIN
                    information_schema.constraint_column_usage AS als_cns_col_usg
                    ON  als_cns_tbl.constraint_name = als_cns_col_usg.constraint_name
                    AND als_cns_tbl.table_schema = als_cns_col_usg.table_schema
                WHERE
                    als_cns_tbl.table_schema = :$INPUT_SCHEMA_NAME
                AND als_cns_tbl.constraint_type = 'FOREIGN KEY'
            )
            SELECT DISTINCT
                qry_tbl_col.table_fqn           AS $OUTPUT_TABLE_FQN,
                qry_tbl_col.table_name          AS $OUTPUT_TABLE_NAME,
                qry_tbl_col.table_comment       AS $OUTPUT_TABLE_COMMENT,
                qry_tbl_col.table_rows          AS $OUTPUT_TABLE_ROWS,
                qry_tbl_col.column_fqn          AS $OUTPUT_COLUMN_FQN,
                qry_tbl_col.column_name         AS $OUTPUT_COLUMN_NAME,
                qry_tbl_col.column_type         AS $OUTPUT_COLUMN_TYPE,
                qry_tbl_col.column_nullable     AS $OUTPUT_COLUMN_NULLABLE,
                qry_tbl_col.column_default      AS $OUTPUT_COLUMN_DEFAULT,
                qry_tbl_col.column_comment      AS $OUTPUT_COLUMN_COMMENT,
                als_rel_prt.parent_table_name   AS $OUTPUT_PARENT_TABLE_NAME,
                als_rel_prt.parent_column_name  AS $OUTPUT_PARENT_COLUMN_NAME,
                als_rel_cld.child_table_name    AS $OUTPUT_CHILD_TABLE_NAME,
                als_rel_cld.child_column_name   AS $OUTPUT_CHILD_COLUMN_NAME
            FROM
                qry_tbl_col
            LEFT OUTER JOIN
                qry_rel AS als_rel_cld
                ON  qry_tbl_col.schema_name = als_rel_cld.schema_name
                AND qry_tbl_col.table_name  = als_rel_cld.child_table_name
                AND qry_tbl_col.column_name = als_rel_cld.child_column_name
            LEFT OUTER JOIN
                qry_rel AS als_rel_prt
                ON  qry_tbl_col.schema_name = als_rel_prt.schema_name
                AND qry_tbl_col.table_name = als_rel_prt.parent_table_name
                AND qry_tbl_col.column_name = als_rel_prt.parent_column_name
            """.trimIndent(), mapOf(
                INPUT_SCHEMA_NAME to schemaName,
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
                                }
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
            tbl.table_schema = :$INPUT_SCHEMA_NAME
        AND tbl.table_type = 'BASE TABLE'
        """.trimIndent(), mapOf(
            INPUT_SCHEMA_NAME to schemaName
    )) { resultSet, _ ->
        TablePhysicalName(resultSet.getString(OUTPUT_TABLE_NAME)) to resultSet.getLong(OUTPUT_TABLE_ROWS)
    }.toMap()

    override fun findReferencedCountFromChildrenMap(): Map<TablePhysicalName, Int> = jdbcOperations.query("""
        SELECT
            parent_table        AS $OUTPUT_TABLE_NAME,
            count(child_column) AS $OUTPUT_REFERENCE_COUNT
        FROM
            (
                SELECT
                    tbl_cnstr.table_name        AS child_table,
                    col_usg.column_name         AS child_column,
                    cnstr_col_usg.table_name    AS parent_table,
                    cnstr_col_usg.column_name   AS parent_column
                FROM
                    information_schema.table_constraints tbl_cnstr
                JOIN
                    information_schema.key_column_usage col_usg
                    ON  tbl_cnstr.constraint_name = col_usg.constraint_name
                    AND tbl_cnstr.table_schema = col_usg.table_schema
                JOIN
                    information_schema.constraint_column_usage AS cnstr_col_usg
                    ON  tbl_cnstr.constraint_name = cnstr_col_usg.constraint_name
                    AND tbl_cnstr.table_schema = cnstr_col_usg.table_schema
                WHERE
                    tbl_cnstr.table_schema = :$INPUT_SCHEMA_NAME
                AND tbl_cnstr.constraint_type = 'FOREIGN KEY'
            ) parent_child_cnstr
        GROUP BY
            parent_table
        """.trimIndent(), mapOf(
            INPUT_SCHEMA_NAME to schemaName
    )) { resultSet, _ ->
        TablePhysicalName(resultSet.getString(OUTPUT_TABLE_NAME)) to resultSet.getInt(OUTPUT_REFERENCE_COUNT)
    }.toMap()

    override fun findReferencingCountToParentMap(): Map<TablePhysicalName, Int> = jdbcOperations.query("""
        SELECT
            child_table             AS $OUTPUT_TABLE_NAME,
            count(parent_column)    AS $OUTPUT_REFERENCE_COUNT
        FROM
            (
                SELECT
                    tbl_cnstr.table_name        AS child_table,
                    col_usg.column_name         AS child_column,
                    cnstr_col_usg.table_name    AS parent_table,
                    cnstr_col_usg.column_name   AS parent_column
                FROM
                    information_schema.table_constraints tbl_cnstr
                JOIN
                    information_schema.key_column_usage col_usg
                    ON  tbl_cnstr.constraint_name = col_usg.constraint_name
                    AND tbl_cnstr.table_schema = col_usg.table_schema
                JOIN
                    information_schema.constraint_column_usage AS cnstr_col_usg
                    ON  tbl_cnstr.constraint_name = cnstr_col_usg.constraint_name
                    AND tbl_cnstr.table_schema = cnstr_col_usg.table_schema
                WHERE
                    tbl_cnstr.table_schema = :$INPUT_SCHEMA_NAME
                AND tbl_cnstr.constraint_type = 'FOREIGN KEY'
            ) parent_child_cnstr
        GROUP BY
            child_table
        """.trimIndent(), mapOf(
            INPUT_SCHEMA_NAME to schemaName
    )) { resultSet, _ ->
        TablePhysicalName(resultSet.getString(OUTPUT_TABLE_NAME)) to resultSet.getInt(OUTPUT_REFERENCE_COUNT)
    }.filter { it.second > 0 }.toMap()

    override fun findStatementByName(name: TablePhysicalName): Statement = Statement("<NOT SUPPORTED>")
}
