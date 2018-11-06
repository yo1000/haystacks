package com.yo1000.haystacks.web.service

import com.yo1000.haystacks.core.service.TableDomainService
import com.yo1000.haystacks.core.valueobject.FullyQualifiedName
import com.yo1000.haystacks.core.valueobject.Note
import com.yo1000.haystacks.core.valueobject.TablePhysicalName
import com.yo1000.haystacks.web.resource.SearchResult
import com.yo1000.haystacks.web.resource.Table
import com.yo1000.haystacks.web.resource.TableOutline

class TableApplicationService(
        private val tableDomainService: TableDomainService
) {
    fun getTableOutlines(): List<TableOutline> {
        val notes = tableDomainService.getNotesMap()

        return tableDomainService.getTableOutlines().map {
            TableOutline(
                    fqn = it.names.fullyQualifiedName.value,
                    name = it.names.physicalName.value,
                    columnCount = it.columnCount,
                    rowCount = it.rowCount,
                    parentCount = it.referencingCountToParent,
                    childCount = it.referencedCountFromChildren,
                    comment = it.names.logicalName.value,
                    note = notes[it.names.fullyQualifiedName]?.value ?: ""
            )
        }
    }

    fun getTableByName(name: String): Table {
        val table = tableDomainService.getTable(TablePhysicalName(name))
        val indexes = tableDomainService.getIndexes(TablePhysicalName(name))
        val statement = tableDomainService.getStatement(TablePhysicalName(name))
        val notes = tableDomainService.getNotesMapByTable(table.names.fullyQualifiedName)

        return Table(
                fqn = table.names.fullyQualifiedName.value,
                name = table.names.physicalName.value,
                comment = table.names.logicalName.value,
                columns = table.columns.map {
                    Table.Column(
                            fqn = it.names.fullyQualifiedName.value,
                            name = it.names.physicalName.value,
                            type = it.type,
                            nullable = it.nullable,
                            default = it.default,
                            parent = it.parent?.let {
                                Table.Reference(
                                        table = it.tablePhysicalName.value,
                                        column = it.columnPhysicalName.value
                                )
                            },
                            children = it.children.map {
                                Table.Reference(
                                        table = it.tablePhysicalName.value,
                                        column = it.columnPhysicalName.value
                                )
                            },
                            comment = it.names.logicalName.value,
                            note = notes[it.names.fullyQualifiedName]?.value ?: ""
                    )
                },
                indexes = indexes.map {
                    Table.Index(
                            fqn = it.names.fullyQualifiedName.value,
                            name = it.names.physicalName.value,
                            type = it.type.name,
                            columns = it.columnNames.map {
                                Table.Reference(
                                        table = table.names.physicalName.value,
                                        column = it.value
                                )
                            },
                            comment = it.names.logicalName.value,
                            note = notes[it.names.fullyQualifiedName]?.value ?: ""
                    )
                },
                statement = statement.value,
                note = notes[table.names.fullyQualifiedName]?.value ?: ""
        )
    }

    fun search(q: List<String>): List<SearchResult> = tableDomainService.find(*q.toTypedArray()).map {
        val noteMap = tableDomainService.getNotesMap()
        SearchResult(
                table = SearchResult.Table(
                        name = it.tableNames.physicalName.value,
                        comment = it.tableNames.logicalName.value,
                        note = noteMap[it.tableNames.fullyQualifiedName]?.value ?: ""
                ),
                columns = it.columnNamesList.map {
                    SearchResult.Column(
                            name = it.physicalName.value,
                            comment = it.logicalName.value,
                            note = noteMap[it.fullyQualifiedName]?.value ?: ""
                    )
                }
        )
    }

    fun setNote(fullyQualifiedName: String, note: String) {
        tableDomainService.setNote(FullyQualifiedName(fullyQualifiedName), Note(note))
    }
}