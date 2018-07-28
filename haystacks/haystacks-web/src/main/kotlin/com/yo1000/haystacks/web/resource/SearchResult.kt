package com.yo1000.haystacks.web.resource

/**
 *
 * @author yo1000
 */
data class SearchResult(
        val table: Table,
        val columns: List<Column>
) {
    data class Table(
            val name: String,
            val comment: String
    )

    data class Column(
            val name: String,
            val comment: String
    )
}
