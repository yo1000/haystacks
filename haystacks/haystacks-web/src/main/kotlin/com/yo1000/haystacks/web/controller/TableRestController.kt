package com.yo1000.haystacks.web.controller

import com.yo1000.haystacks.core.service.TableService
import com.yo1000.haystacks.core.valueobject.TablePhysicalName
import com.yo1000.haystacks.web.resource.Table
import com.yo1000.haystacks.web.resource.TableOutline
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

/**
 *
 * @author yo1000
 */
@RequestMapping("/api/tables")
open class TableRestController(
        private val tableService: TableService,
        private val dataSourceName: String
) {
    @GetMapping("")
    fun get(): List<TableOutline> = tableService.getTableOutlines().map {
        TableOutline(
                name = it.names.physicalName.value,
                columnCount = it.columnCount,
                rowCount = it.rowCount,
                parentCount = it.referencingCountToParent,
                childCount = it.referencedCountFromChildren,
                comment = it.names.logicalName.value
        )
    }

    @GetMapping("/{name}")
    fun getTableByName1(@PathVariable name: String): Table {
        val table = tableService.getTable(TablePhysicalName(name))
        val indexes = tableService.getIndexes(TablePhysicalName(name))
        val statement = tableService.getStatement(TablePhysicalName(name))

        return Table(
                name = table.names.physicalName.value,
                comment = table.names.logicalName.value,
                columns = table.columns.map {
                    Table.Column(
                            name = it.names.physicalName.value,
                            type = it.type,
                            nullable = it.nullable,
                            default = it.default,
                            parent = it.parent?.let {
                                "${it.tablePhysicalName.value}.${it.columnPhysicalName.value}"
                            },
                            children = it.children.map {
                                "${it.tablePhysicalName.value}.${it.columnPhysicalName.value}"
                            },
                            comment = it.names.logicalName.value
                    )
                },
                indexes = indexes.map {
                    Table.Index(
                            name = it.names.physicalName.value,
                            type = it.type.name,
                            columns = it.columnNames.map {
                                it.value
                            },
                            comment = it.names.logicalName.value
                    )
                },
                statement = statement.value
        )
    }
}
