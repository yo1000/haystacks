package com.yo1000.haystacks.domain.entity

/**
 *
 * @author yo1000
 */
class FoundNamednessMap(vararg objects: Pair<TableNamedness, ColumnNamednessList>)
    : Map<TableNamedness, ColumnNamednessList> by mapOf(*objects)
