package com.yo1000.haystacks.domain.entity

/**
 *
 * @author yo1000
 */
class FoundNamesMap(vararg objects: Pair<TableNames, ColumnNamesList>)
    : Map<TableNames, ColumnNamesList> by mapOf(*objects)
