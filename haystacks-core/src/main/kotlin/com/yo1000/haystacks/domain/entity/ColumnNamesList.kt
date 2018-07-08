package com.yo1000.haystacks.domain.entity

/**
 *
 * @author yo1000
 */
class ColumnNamesList(vararg names: ColumnNames)
    : List<ColumnNames> by listOf(*names)
