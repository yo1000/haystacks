package com.yo1000.haystacks.web.controller

import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

/**
 * @author yo1000
 */
@RequestMapping("/search")
open class SearchController(
        private val dataSourceName: String
) {
    @GetMapping("")
    fun get(model: Model, @RequestParam q: List<String>): String {
        model.addAttribute("dataSourceName", dataSourceName)
        model.addAttribute("queries", q)
        return "search"
    }
}
