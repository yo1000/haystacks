package com.yo1000.haystacks.domain.repository

/**
 *
 * @author yo1000
 */
interface TableRepository {
    fun findParentRelationCount(vararg query: String): Map<String, Int>
    fun findChildrenRelationCount(vararg query: String): Map<String, Int>
    fun showStatement(name: String): String
}