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
import org.springframework.cache.annotation.Cacheable

/**
 *
 * @author yo1000
 */
open class TableDomainService(
        private val tableRepository: TableRepository,
        private val indexRepository: IndexRepository,
        private val noteRepository: NoteRepository
) {
    companion object {
        const val CACHE_NAME_TABLE_OUTLINES = "TableDomainService.TableOutlines"
    }

    @Cacheable(CACHE_NAME_TABLE_OUTLINES)
    open fun getTableOutlines(): List<TableOutline> {
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

    open fun getTable(name: TablePhysicalName): Table = tableRepository.findTable(name)
    open fun getIndexes(name: TablePhysicalName): List<Index> = indexRepository.findByTableName(name)
    open fun getStatement(name: TablePhysicalName): Statement = tableRepository.findStatementByName(name)

    open fun getNotesMap(): Map<FullyQualifiedName, Note> = noteRepository.findNoteMap()
    open fun getNotesMapByTable(fullyQualifiedTableName: FullyQualifiedName): Map<FullyQualifiedName, Note> =
            noteRepository.findNoteMapByFullyQualifiedTableName(fullyQualifiedTableName)
    open fun setNote(fqn: FullyQualifiedName, note: Note) = noteRepository.save(fqn, note)

    open fun find(vararg q: String): List<FoundNames> = tableRepository.findNames(*(
            noteRepository.findNoteMap().entries.filter { entry ->
                q.any { entry.value.value.contains(it) }
            }.map {
                it.key.value
            } + q).toTypedArray()
    )
}
