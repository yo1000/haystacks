package com.yo1000.haystacks.domain.service

import com.yo1000.haystacks.domain.entity.Table
import com.yo1000.haystacks.domain.entity.TableNamedness
import com.yo1000.haystacks.domain.entity.TableOutline
import com.yo1000.haystacks.domain.repository.TableRepository
import com.yo1000.haystacks.domain.valueobject.TablePhysicalName
import org.springframework.stereotype.Service

/**
 *
 * @author yo1000
 */
@Service
class TableService(
        private val tableRepository: TableRepository
) {
    fun getTable(name: TablePhysicalName): Table = tableRepository.findTableNamedness(name).let {
        Table(
                namedness = it,
                columns = tableRepository.findColumns(name),
                indexes = tableRepository.findIndexes(name),
                rowCount = tableRepository.findRowCountMap()[it.physicalName] ?: 0L,
                statement = tableRepository.findStatement(name)
        )
    }

    fun getTablesAll(): List<TableOutline> {
        val columnCountMap = tableRepository.findColumnCountMap()
        val rowCountMap = tableRepository.findRowCountMap()
        val childrenCountMap = tableRepository.findReferencedCountFromChildrenMap()
        val parentCountMap = tableRepository.findReferencingCountToParentMap()

        return tableRepository.findNamedness().map {
            TableOutline(
                    namedness = TableNamedness(
                            tablePhysicalName = TablePhysicalName(it.key.physicalName.value),
                            logicalName = it.key.logicalName
                    ),
                    columnCount = columnCountMap[it.key.physicalName] ?: 0,
                    rowCount = rowCountMap[it.key.physicalName] ?: 0L,
                    referencedCountFromChildren = childrenCountMap[it.key.physicalName] ?: 0,
                    referencingCountToParent = parentCountMap[it.key.physicalName] ?: 0
            )
        }
    }

    fun findTables(vararg q: String): List<Table> {
        TODO("thinking..")
    }
}