package com.yo1000.haystacks.web.controller

import com.yo1000.haystacks.core.service.TableDomainService
import com.yo1000.haystacks.web.service.TableApplicationService
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

/**
 * @author yo1000
 */
@RequestMapping("/search")
open class SearchController(
        private val ssr: Boolean,
        private val dataSourceName: String,
        private val tableApplicationService: TableApplicationService
) {
    @GetMapping("")
    fun get(model: Model, @RequestParam q: List<String>): String {
        model.addAttribute("dataSourceName", dataSourceName)
        model.addAttribute("queries", q)

        if (ssr) {
            model.addAttribute("results", tableApplicationService.search(q))
            return "search.ssr"
        } else {
            return "search"
        }
    }
}
