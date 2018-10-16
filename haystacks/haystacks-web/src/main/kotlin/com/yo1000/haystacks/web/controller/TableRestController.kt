package com.yo1000.haystacks.web.controller

import com.yo1000.haystacks.web.resource.Table
import com.yo1000.haystacks.web.resource.TableOutline
import com.yo1000.haystacks.web.service.TableApplicationService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

/**
 *
 * @author yo1000
 */
@RequestMapping("/api/tables")
open class TableRestController(
        private val tableApplicationService: TableApplicationService
) {
    @GetMapping("")
    fun get(): List<TableOutline> = tableApplicationService.getTableOutlines()

    @GetMapping("/{name}")
    fun getTableByName(@PathVariable name: String): Table = tableApplicationService.getTableByName(name)
}
