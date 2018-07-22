package com.yo1000.haystacks.web

import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

/**
 * @author yo1000
 */
@RequestMapping("/table")
open class TableController(
        private val dataSourceName: String
) {
    @GetMapping("")
    fun get(model: Model): String {
        model.addAttribute("dataSourceName", dataSourceName)
        return "tables"
    }
}
