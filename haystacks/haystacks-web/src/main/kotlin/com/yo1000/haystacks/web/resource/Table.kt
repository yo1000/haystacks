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
            val parent: Reference?,
            val children: List<Reference>,
            val comment: String
    )

    data class Index(
            val name: String,
            val type: String,
            val columns: List<Reference>,
            val comment: String
    )

    data class Reference(
            val table: String,
            val column: String
    )
}
