package com.yo1000.haystacks.web

import com.yo1000.haystacks.core.entity.Index
import com.yo1000.haystacks.core.entity.Table
import com.yo1000.haystacks.core.entity.TableOutline
import com.yo1000.haystacks.core.service.TableService
import com.yo1000.haystacks.core.valueobject.Statement
import com.yo1000.haystacks.core.valueobject.TablePhysicalName
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

/**
 *
 * @author yo1000
 */
@RequestMapping("/tables")
open class TableRestController(
        private val tableService: TableService
) {
    @GetMapping("")
    fun get(): List<TableOutline> = tableService.getTableOutlines()

    @GetMapping("/{name}")
    fun getTableByName(@PathVariable name: String): Table = tableService.getTable(TablePhysicalName(name))

    @GetMapping("/{name}/indexes")
    fun getIndexesByName(@PathVariable name: String): List<Index> = tableService.getIndexes(TablePhysicalName(name))

    @GetMapping("/{name}/statement")
    fun getStatementByName(@PathVariable name: String): Statement = tableService.getStatement(TablePhysicalName(name))
}
