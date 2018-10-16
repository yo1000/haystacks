package com.yo1000.haystacks.web.controller

import com.yo1000.haystacks.web.resource.SearchResult
import com.yo1000.haystacks.web.service.TableApplicationService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

/**
 *
 * @author yo1000
 */
@RequestMapping("/api/search")
open class SearchRestController(
        private val tableApplicationService: TableApplicationService
) {
    @GetMapping("")
    fun get(@RequestParam q: List<String>): List<SearchResult> = tableApplicationService.search(q)
}
