package com.yo1000.haystacks.application

import com.yo1000.haystacks.domain.entity.Index
import com.yo1000.haystacks.domain.entity.Table
import com.yo1000.haystacks.domain.entity.TableOutline
import com.yo1000.haystacks.domain.service.TableService
import com.yo1000.haystacks.domain.valueobject.Statement
import com.yo1000.haystacks.domain.valueobject.TablePhysicalName
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 *
 * @author yo1000
 */
@RestController
@RequestMapping("/tables")
class TableRestController(
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
