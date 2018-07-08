package com.yo1000.haystacks.domain.service

import com.yo1000.haystacks.domain.entity.FoundNamesMap
import com.yo1000.haystacks.domain.entity.Table
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
    fun getTable(name: TablePhysicalName): Table = tableRepository.findTable(name)

    fun getTableOutlines(): List<TableOutline> {
        val columnCountMap = tableRepository.findColumnCountMap()
        val rowCountMap = tableRepository.findRowCountMap()
        val childrenCountMap = tableRepository.findReferencedCountFromChildrenMap()
        val parentCountMap = tableRepository.findReferencingCountToParentMap()

        return tableRepository.findTableNamesAll().map {
            TableOutline(
                    names = it,
                    columnCount = columnCountMap[it.physicalName] ?: 0,
                    rowCount = rowCountMap[it.physicalName] ?: 0L,
                    referencedCountFromChildren = childrenCountMap[it.physicalName] ?: 0,
                    referencingCountToParent = parentCountMap[it.physicalName] ?: 0
            )
        }
    }

    fun find(vararg q: String): FoundNamesMap = tableRepository.findNames(*q)
}