package com.yo1000.haystacks.domain.service

import com.yo1000.haystacks.domain.entity.Table
import com.yo1000.haystacks.domain.repository.TableRepository
import com.yo1000.haystacks.domain.valueobject.TableName
import org.springframework.stereotype.Service

/**
 *
 * @author yo1000
 */
@Service
class TableService(
        private val tableRepository: TableRepository
) {
    fun getTable(name: TableName): Table = tableRepository.findTable(name)
    fun getTablesAll(): List<Table> = tableRepository.findTables()
    fun findTables(vararg q: String): List<Table> = tableRepository.findTables(*q)
}