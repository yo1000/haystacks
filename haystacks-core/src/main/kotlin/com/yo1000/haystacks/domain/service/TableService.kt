package com.yo1000.haystacks.domain.service

import com.yo1000.haystacks.domain.entity.Table
import com.yo1000.haystacks.domain.entity.TableOutline
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
    fun getTable(name: TableName): Table {
        TODO("thinking..")
    }

    fun getTablesAll(): List<TableOutline> {
        val columnCountMap = tableRepository.findColumnCountMap()
        val rowCountMap = tableRepository.findRowCountMap()
        val childrenCountMap = tableRepository.findReferencedCountFromChildrenMap()
        val parentCountMap = tableRepository.findReferencingCountToParentMap()

        return tableRepository.findTableObjects().map {
            TableOutline(
                    name = TableName(it.name.value),
                    comment = it.comment,
                    columnCount = columnCountMap[it.name] ?: 0,
                    rowCount = rowCountMap[it.name] ?: 0L,
                    referencedCountFromChildren = childrenCountMap[it.name] ?: 0,
                    referencingCountToParent = parentCountMap[it.name] ?: 0
            )
        }
    }

    fun findTables(vararg q: String): List<Table> {
        TODO("thinking..")
    }
}