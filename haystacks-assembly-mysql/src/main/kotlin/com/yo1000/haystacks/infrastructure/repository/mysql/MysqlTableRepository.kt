package com.yo1000.haystacks.infrastructure.repository.mysql

import com.yo1000.haystacks.domain.entity.FoundNamesMap
import com.yo1000.haystacks.domain.entity.Table
import com.yo1000.haystacks.domain.entity.TableNames
import com.yo1000.haystacks.domain.repository.TableRepository
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
    override fun findNames(vararg q: String): FoundNamesMap {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findTableNamesAll(): List<TableNames> = jdbcTemplate.query("""
        SELECT
            tbl.table_name    AS `name`,
            tbl.table_comment AS `comment`
        FROM
            information_schema.tables tbl
        WHERE
            tbl.table_schema = :schemaName
        AND
            tbl.table_type = 'BASE TABLE'
        """.trimIndent(), mapOf(
            "schemaName" to dataSourceProperties.name
    )) { resultSet, _ ->
        TableNames(
                physicalName = TablePhysicalName(resultSet.getString("name")),
                logicalName = LogicalName(resultSet.getString("comment"))
        )
    }

    override fun findTable(name: TablePhysicalName): Table {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findColumnCountMap(): Map<TablePhysicalName, Int> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findRowCountMap(): Map<TablePhysicalName, Long>  = jdbcTemplate.query("""
        SELECT
            tbl.table_name    AS `name`,
            tbl.table_rows    AS `rowSize`
        FROM
            information_schema.tables tbl
        WHERE
            tbl.table_schema = :schemaName
        AND
            tbl.table_type = 'BASE TABLE'
        """.trimIndent(), mapOf(
            "schemaName" to dataSourceProperties.name
    )) { resultSet, _ ->
        TablePhysicalName(resultSet.getString("name")) to resultSet.getLong("rowSize")
    }.toMap()

    override fun findReferencedCountFromChildrenMap(): Map<TablePhysicalName, Int> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findReferencingCountToParentMap(): Map<TablePhysicalName, Int> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}