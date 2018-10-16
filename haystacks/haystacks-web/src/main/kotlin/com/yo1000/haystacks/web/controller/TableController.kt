package com.yo1000.haystacks.web.controller

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
        private val dataSourceName: String
) {
    @GetMapping("")
    fun get(model: Model): String {
        model.addAttribute("dataSourceName", dataSourceName)
        return "tables"
    }

    @GetMapping("/{tableName}")
    fun getByName(model: Model, @PathVariable tableName: String): String {
        model.addAttribute("dataSourceName", dataSourceName)
        model.addAttribute("tableName", tableName)

        return "table"
    }
}
