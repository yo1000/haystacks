package com.yo1000.haystacks.core.repository

import com.yo1000.haystacks.core.valueobject.FullyQualifiedName
import com.yo1000.haystacks.core.valueobject.Note
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Path
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

interface NoteRepository {
    fun findNoteMap(): Map<FullyQualifiedName, Note>
    fun findByTableColumnPhysicalNames(fullyQualifiedName: FullyQualifiedName): Note
    fun save(fullyQualifiedName: FullyQualifiedName, note: Note): Note
}

class PropertiesNoteRepository(
        private val propertiesFilePath: Path
) : NoteRepository {
    companion object {
        val noteMapLocker = ReentrantLock()
    }

    private var properties: Properties
    private var noteMap: Map<FullyQualifiedName, Note>

    init {
        properties = reload()
        noteMap = properties.toNoteMap()
    }

    override fun findNoteMap(): Map<FullyQualifiedName, Note> = noteMap

    override fun findByTableColumnPhysicalNames(
            fullyQualifiedName: FullyQualifiedName): Note =
            noteMap[fullyQualifiedName] ?: Note("")

    override fun save(fullyQualifiedName: FullyQualifiedName, note: Note): Note {
        noteMapLocker.withLock {
            properties.setProperty(fullyQualifiedName.value, note.value)
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

    private fun Properties.toNoteMap(): Map<FullyQualifiedName, Note> = this.map {
        FullyQualifiedName(it.key as String) to Note(it.value as String)
    }.toMap()
}
