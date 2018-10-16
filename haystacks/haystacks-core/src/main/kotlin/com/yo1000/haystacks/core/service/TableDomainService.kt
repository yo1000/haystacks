package com.yo1000.haystacks.core.service

import com.yo1000.haystacks.core.entity.FoundNames
import com.yo1000.haystacks.core.entity.Index
import com.yo1000.haystacks.core.entity.Table
import com.yo1000.haystacks.core.entity.TableOutline
import com.yo1000.haystacks.core.repository.IndexRepository
import com.yo1000.haystacks.core.repository.NoteRepository
import com.yo1000.haystacks.core.repository.TableRepository
import com.yo1000.haystacks.core.valueobject.FullyQualifiedName
import com.yo1000.haystacks.core.valueobject.Note
import com.yo1000.haystacks.core.valueobject.Statement
import com.yo1000.haystacks.core.valueobject.TablePhysicalName

/**
 *
 * @author yo1000
 */
class TableDomainService(
        private val tableRepository: TableRepository,
        private val indexRepository: IndexRepository,
        private val noteRepository: NoteRepository
) {
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

    fun getTable(name: TablePhysicalName): Table = tableRepository.findTable(name)
    fun getIndexes(name: TablePhysicalName): List<Index> = indexRepository.findByTableName(name)
    fun getStatement(name: TablePhysicalName): Statement = tableRepository.findStatementByName(name)

    fun getNotesMap(): Map<FullyQualifiedName, Note> = noteRepository.findNoteMap()
    fun getNotesMapByTable(fullyQualifiedTableName: FullyQualifiedName): Map<FullyQualifiedName, Note> =
            noteRepository.findNoteMapByFullyQualifiedTableName(fullyQualifiedTableName)
    fun setNote(fqn: FullyQualifiedName, note: Note) = noteRepository.save(fqn, note)

    fun find(vararg q: String): List<FoundNames> = tableRepository.findNames(*q)
}
