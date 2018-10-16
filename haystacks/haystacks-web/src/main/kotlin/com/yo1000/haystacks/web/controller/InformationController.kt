package com.yo1000.haystacks.web.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

/**
 * @author yo1000
 */
@RequestMapping("/info")
open class InformationController(
        private val ssr: Boolean
) {
    @GetMapping("")
    fun get(): String {
        return "info"
    }
}
