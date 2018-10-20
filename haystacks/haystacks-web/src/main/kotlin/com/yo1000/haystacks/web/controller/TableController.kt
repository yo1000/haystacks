package com.yo1000.haystacks.web.controller

import com.yo1000.haystacks.core.service.TableDomainService
import com.yo1000.haystacks.web.service.TableApplicationService
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

/**
 * @author yo1000
 */
@RequestMapping("/table")
open class TableController(
        private val ssr: Boolean,
        private val tableApplicationService: TableApplicationService
) {
    @GetMapping("")
    fun get(model: Model): String = if (ssr) {
        model.addAttribute("tables", tableApplicationService.getTableOutlines())
        "tables.ssr"
    } else {
        "tables"
    }

    @GetMapping("/{tableName}")
    fun getByName(model: Model, @PathVariable tableName: String): String {
        model.addAttribute("tableName", tableName)

        if (ssr) {
            model.addAttribute("table", tableApplicationService.getTableByName(tableName))
            return "table.ssr"
        } else {
            return "table"
        }
    }
}
