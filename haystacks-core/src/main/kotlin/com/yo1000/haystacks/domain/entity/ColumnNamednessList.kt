package com.yo1000.haystacks.domain.entity

/**
 *
 * @author yo1000
 */
class ColumnNamednessList(vararg columnNamedness: ColumnNamedness)
    : List<ColumnNamedness> by listOf(*columnNamedness)
