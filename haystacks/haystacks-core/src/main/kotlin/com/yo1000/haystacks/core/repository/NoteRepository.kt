package com.yo1000.haystacks.core.repository

import com.yo1000.haystacks.core.entity.TableColumnPhysicalNames
import com.yo1000.haystacks.core.valueobject.ColumnPhysicalName
import com.yo1000.haystacks.core.valueobject.Note
import com.yo1000.haystacks.core.valueobject.TablePhysicalName
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Path
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

interface NoteRepository {
    fun findNoteMap(): Map<TableColumnPhysicalNames, Note>
    fun findByTableColumnPhysicalNames(
            tablePhysicalName: TablePhysicalName,
            columnPhysicalName: ColumnPhysicalName): Note
    fun save(
            tablePhysicalName: TablePhysicalName,
            columnPhysicalName: ColumnPhysicalName,
            note: Note): Note
}

class PropertiesNoteRepository(
        private val propertiesFilePath: Path
) : NoteRepository {
    companion object {
        val noteMapLocker = ReentrantLock()
    }

    private var properties: Properties
    private var noteMap: Map<TableColumnPhysicalNames, Note>

    init {
        properties = reload()
        noteMap = properties.toNoteMap()
    }

    override fun findNoteMap(): Map<TableColumnPhysicalNames, Note> = noteMap

    override fun findByTableColumnPhysicalNames(
            tablePhysicalName: TablePhysicalName,
            columnPhysicalName: ColumnPhysicalName): Note =
            noteMap[TableColumnPhysicalNames(tablePhysicalName, columnPhysicalName)] ?: Note("")

    override fun save(
            tablePhysicalName: TablePhysicalName,
            columnPhysicalName: ColumnPhysicalName,
            note: Note): Note {
        noteMapLocker.withLock {
            properties.setProperty(
                    "${tablePhysicalName.value}.${columnPhysicalName.value}",
                    note.value)
            properties.store(FileOutputStream(propertiesFilePath.toFile()), null)
            properties = reload()
            noteMap = properties.toNoteMap()
        }

        return note
    }

    private fun reload(): Properties {
        val props = Properties()
        FileInputStream(propertiesFilePath.toFile()).use {
            props.load(it)
        }
        return props
    }

    private fun Properties.toNoteMap(): Map<TableColumnPhysicalNames, Note> = this.mapNotNull {
        val tableColumn = (it.key as String).split('.')
        if (tableColumn.size != 2) null
        else TableColumnPhysicalNames(
                tablePhysicalName = TablePhysicalName(tableColumn[0]),
                columnPhysicalName = ColumnPhysicalName(tableColumn[1])
        ) to Note(it.value as String)
    }.toMap()
}
