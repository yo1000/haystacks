package com.yo1000.haystacks.web.resource

/**
 *
 * @author yo1000
 */
data class TableOutline(
        val name: String,
        val columnCount: Int,
        val rowCount: Long,
        val parentCount: Int,
        val childCount: Int,
        val comment: String,
        val note: String
)
