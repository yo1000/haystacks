package com.yo1000.haystacks.web.resource

/**
 *
 * @author yo1000
 */
data class Table(
        val name: String,
        val comment: String,
        val columns: List<Column>,
        val indexes: List<Index>,
        val statement: String
) {
    data class Column(
            val name: String,
            val type: String,
            val nullable: Boolean,
            val default: String?,
            val parent: String?,
            val children: List<String>,
            val comment: String
    )

    data class Index(
            val name: String,
            val type: String,
            val columns: List<String>,
            val comment: String
    )
}
